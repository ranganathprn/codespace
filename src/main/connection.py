#Scenario-1 using postgresql Driver (Not Working Connectin timeout)
#Placed the postgresql driver in s3 and configure in DependentJars path in glue job s3://<bucket>/lib/postgresql-42.2.23.jar

jdbc_url= 'jdbc:postgresql://<hostname>:5432/postgres'
db_properties = {}
db_properties['user']= 'postgres'
db_properties['password']= getfromssm()
db_properties['url']= jdbc_url
db_properties['driver']= "org.postgresql.Driver"
table="(SELECT * from public.testdata) as t1"
df = spark.read.jdbc(url=jdbc_url,table=table,properties=db_properties)
print(df)


#Scenario-2 (Working approach)
from py4j.java_gateway import java_import

def getDBConnection():
    # preconfigured configured connection under AWSGlue -> Databases -> Connections (trader-product-glue)
    source_jdbc_conf = glueContext.extract_jdbc_conf('trader-product-glue')  
    java_import(sc._gateway.jvm,"java.sql.Connection")
    java_import(sc._gateway.jvm,"java.sql.DatabaseMetaData")
    java_import(sc._gateway.jvm,"java.sql.DriverManager")
    java_import(sc._gateway.jvm,"java.sql.SQLException")
    java_import(sc._gateway.jvm,"java.sql.Statement")
    java_import(sc._gateway.jvm,"java.sql.Result")

    url=source_jdbc_conf.get('url')+"/postgres"
    print('-----URL constructed------',url)
    conn = sc._gateway.jvm.DriverManager.getConnection(url, source_jdbc_conf.get('user'), source_jdbc_conf.get('password'))
    return conn
