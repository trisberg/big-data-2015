Simple Hadoop Web App
=====================

Build using:

    mvn clean package


Run local using:

    java -jar target/hello-cloud-0.1.0.jar


Build Docker image (you need a Docker Hub account):

    docker build -t hello-cloud .
    docker tag -f hello-cloud $USER/hello-cloud
    docker push $USER/hello-cloud

Start lattice (use v0.2.3)

Start Hadoop (Docker image or VM or local)

Create lattice app:

Using Hadoop VM:

    ltc create hello-cloud trisberg/hello-cloud --memory-mb=0 --timeout '4m0s' --env spring_profiles_active=lattice --env hadoop_host=borneo


OR if using Docker image and SOCKS proxy:

  * edit /etc/hosts and have ltchost resolve local IP address for en0

  * start proxy:
    ssh -i ~/.ssh/id_docker_rsa root@$(boot2docker ip) -p 2122 -D ltchost:1099

  * find IP address for the Hadoop Docker container (mine was 172.17.0.3)

  * start lattice app (using the IP address from above in hadoop_host):
    ltc create hello-cloud trisberg/hello-cloud --memory-mb=0 --timeout '4m0s' --env spring_profiles_active=socks-proxy --env hadoop_host=172.17.0.3
