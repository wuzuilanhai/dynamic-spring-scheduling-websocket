#!/bin/bash

project="tutorial"
spider=$1
dir=$2
echo "project="$project
echo "spider="$spider
#先判断环境是否完善
python -V
if [ $? -eq 0 ];then
    echo "python is installed"
else
    echo "python is not installed"
    exit 0
fi
pip -V
if [ $? -eq 0 ];then
    echo "pip is installed"
else
    echo "pip is not installed"
    exit 0
fi
scrapyd -h > /dev/null 2>&1
if [ $? -eq 0 ];then
    echo "scrapyd is installed"
else
    echo "scrapyd is not installed"
    exit 0
fi
scrapyd-deploy -h > /dev/null 2>&1
if [ $? -eq 0 ];then
    echo "scrapyd-deploy is installed"
else
    echo "scrapyd-deploy is not installed"
    exit 0
fi
#再判断scrapyd是否已经启动
psid=0
psid=$(ps aux | grep scrapyd | grep -v grep | awk '{print $2}')
if [[ $psid -ne 0 ]];then
    echo "scrapyd is started"
else
    echo "scrapyd is not started"
    exit 0
fi
#进入python爬虫根目录下面
cd $dir
scrapyd-deploy $project -p $spider
