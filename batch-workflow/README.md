xd-batch-workflow
=================

Spring XD Batch Job Workflow example.

**Note:** 
This example runs in Spring XD

Start Spring XD:
----------------

cd $XD_HOME
JAVA_OPTS="-XX:PermSize=256m -Xmx8g -d64" ./bin/xd-singlenode

Start the Spring XD shell:
--------------------------

cd $XD_HOME/../shell
./bin/xd-shell


Build with:
-----------

    $ mvn clean package

Install from XD Shell with:
---------------------------

    xd:>module upload --type job --name batch-workflow --file <path-to-this-example>/target/batch-workflow-0.2.0.jar

Run from XD Shell with:
-----------------------

    xd>job create --name workflow --definition "batch-workflow" --deploy
    xd>job launch --name workflow --params {"local.file":"/Users/trisberg/SpringOne/input/hadoop-tweets_2014-09-02.txt"}

Alternatively, launch job with a stream looking for files copied to a directory:

    xd>stream create tweetFile --definition "file --ref=true --dir=/Users/trisberg/BigData/input --pattern='*.txt' | transform --expression='{\"local.file\":\"'+#{'payload.getAbsolutePath()'}+'\"}' > queue:job:workflow" --deploy


module upload --type job --name batch-workflow --file /Users/trisberg/Projects/trisberg/big-data-2015/batch-workflow/target/batch-workflow-0.2.0.jar

job launch --name workflow --params {"local.file":"/Users/trisberg/Projects/trisberg/big-data-2015/data/hadoop-tweets_2014-09-02.txt"}

**Note:** 

* Adjust the path for the `local.file` parameter or `--dir` option to what you are using.
* Before launching the job manually or after you start the stream, copy the `data/hadoop-tweets_2014-09-02.txt` file to the input directory you are using.
* Remember that the input file will be deleted after it is copied to HDFS.
