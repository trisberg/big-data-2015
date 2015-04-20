Simple Groovy Hadoop Web App
============================


Build using:

    mvn clean package


Run using:

    java -jar target/hello-cloud-0.1.0.jar



Build Docker image (yneed a Docker Hub account):

    docker build -t hello-cloud .
    docker tag -f hello-cloud $USER/hello-cloud
    docker push $USER/hello-cloud

