#!/usr/bin/env bash

#安装sqlite
yum --version > /dev/null 2>&1
if [ $? -eq 0 ];then
    echo "yum is installed"
else
    echo "yum is not installed"
    exit 0
fi
yum -y install sqlite-devel

#安装python2.7
wget http://python.org/ftp/python/2.7.3/Python-2.7.3.tar.bz2
tar -jxvf Python-2.7.3.tar.bz2
cd Python-2.7.3
./configure
make all
make install
make clean
make distclean
mv /usr/bin/python /usr/bin/python2.6.6
ln -s /usr/local/bin/python2.7 /usr/bin/python

cd ..

#安装setuptools
wget --no-check-certificate https://bootstrap.pypa.io/ez_setup.py
python ez_setup.py --insecure

#安装pip
wget https://pypi.python.org/packages/11/b6/abcb525026a4be042b486df43905d6893fb04f05aac21c32c638e939e447/pip-9.0.1.tar.gz#md5=35f01da33009719497f01a4ba69d63c9
tar -xf pip-9.0.1.tar.gz
cd pip-8.0.0
python setup.py install

cd ..

#安装Twisted
wget https://pypi.python.org/packages/source/T/Twisted/Twisted-15.2.1.tar.bz2
tar -xjvf Twisted-15.2.1.tar.bz2
cd Twisted-15.2.1
python setup.py install

cd ..

#安装scrapyd
pip install scrapyd

#安装scrapyd-client
pip install scrapyd-client

#开启6800端口接收数据
/sbin/iptables -I INPUT -p tcp --dport 6800 -j ACCEPT
#开启6800端口发送数据
/sbin/iptables -I OUTPUT -p tcp --dport 6800 -j ACCEPT
#保存配置
/etc/rc.d/init.d/iptables save
#重启防火墙服务
/etc/rc.d/init.d/iptables restart

#最后需要修改scrapyd配置文件default_scrapyd.conf中的bind_address为服务器ip地址
echo "最后需要修改scrapyd配置文件default_scrapyd.conf中的bind_address为服务器ip地址"
