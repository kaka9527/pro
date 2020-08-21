nohup java -jar activemq-demo-1.0-SNAPSHOT.jar >log.txt 2&>1 &

fail -f log.txt

查看端口
netstat -tunlp|grep 1883

安装在虚拟机上的CentOS7的时间分为系统时间和硬件时间。二者都修改，重启系统（init 6 )才会永久生效。
修改步骤如下
       1.查看当前系统时间 date
       2.修改当前系统时间 date -s "2018-2-22 19:10:30
       3.查看硬件时间 hwclock --show
       4.修改硬件时间 hwclock --set --date "2018-2-22 19:10:30"
       5.同步系统时间和硬件时间 hwclock --hctosys
       6.保存时钟 clock -w
       7.重启系统（init 6）后便发现系统时间被修改了
时区的修改