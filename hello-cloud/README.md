Simple Hadoop Web App for Cloud
===============================

Build using:

    mvn clean package

Run local using:

    java -jar target/hello-cloud-0.1.0.jar

Build Docker image (you need boot2docker running and to have a Docker Hub account):

    $(boot2docker shellinit)
    docker build -t hello-cloud .
    docker tag -f hello-cloud $USER/hello-cloud
    docker push $USER/hello-cloud

### Demo instructions:

This demo can be run in several ways. You can run locally, in a Docker container or deployed to Lattice. See below for instructions how to run the app.

Some common instructions for the demo:

The app has 5 pages:

* `/` for Hello message
* `/env` for environment
* `/ip` for IP address
* `/fspath` for browsing HDFS 
* `/wc` for word count MapReduce example

Before you run the word count demo you need to do the following:

If you are using the SequenceIQ hadoop Docker image you need to start the Job History server and also set the path for the `hadoop` command. Enter the following after the Docker container starts:

    export PATH=$PATH:/usr/local/hadoop/bin
    /usr/local/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver

Create directories in HDFS on the Hadoop system:

    hadoop fs -mkdir /tmp
    hadoop fs -chmod 1777 /tmp
    hadoop fs -mkdir -p /tmp/hadoop-yarn/staging/history
    hadoop fs -chmod -R 777 /tmp/hadoop-yarn

Prepare example text on the Hadoop system:

    export APP_USER=vcap  (use vcap for lattice, root for docker, $USER for local)
    hadoop fs -mkdir -p /user/$APP_USER/example/input
    vi sample.txt  (add some text)
    hadoop fs -put sample.txt /user/$APP_USER/example/input/sample.txt
    hadoop fs -chown -R $APP_USER /user/$APP_USER

### Running the Docker image:

We are using boot2docker and Docker version 1.5.0. For Docker install see: https://docs.docker.com/installation/

We are using Lattice version v0.2.3. For Lattice install see: https://github.com/cloudfoundry-incubator/lattice

#### Using Docker and Hadoop running in another Docker image

__Start Hadoop Docker image__

    $(boot2docker shellinit)
    docker run -i -t sequenceiq/hadoop-docker:2.6.0 /etc/bootstrap.sh -bash

__Start Spring App Docker image__

First
* look up the ip address of the Hadoop Docker image (mine was 172.17.0.3) and pass that in as the hadoop_host env var below

then start Docker image

    docker run -p 8080 -e spring_profiles_active=docker -e hadoop_host=172.17.0.3 hello-cloud

#### Using Lattice with Hadoop in a VM

__Start hadoop VM__  -- see: https://github.com/trisberg/big-data-2015/blob/master/InstallingHadoop.asciidoc

__Start lattice__

    vagrant up
    ltc create hello-cloud trisberg/hello-cloud --memory-mb=0 --timeout '4m0s' --env spring_profiles_active=lattice --env hadoop_host=borneo


#### Using Lattice with Hadoop running in a Docker image using SOCKS proxy

__Start Hadoop Docker image__

    $(boot2docker shellinit)
	docker run -i -p 2122:2122 -t sequenceiq/hadoop-docker:2.6.0 /etc/bootstrap.sh -bash
	
__Start SOCKS proxy__

First
* copy content of `/root/.ssh/id_rsa` from Hadoop Docker container to `~/.ssh/id_docker_rsa` on your local system
* add `ltchost` entry to your local `/etc/hosts` and have it resolve to your local IP address

then start the proxy

    ssh -i ~/.ssh/id_docker_rsa root@$(boot2docker ip) -p 2122 -D ltchost:1099

__Start lattice__

First
* look up the ip address of the Hadoop Docker image (mine was 172.17.0.3) and pass that in as the hadoop_host env var below

then start lattice and app

    vagrant up
    ltc create hello-cloud trisberg/hello-cloud --memory-mb=0 --timeout '4m0s' --env spring_profiles_active=socks-proxy --env hadoop_host=172.17.0.3    

