#!/bin/bash
# led 컨트롤러 초기화 시작 
echo 38 > /sys/class/gpio/export
echo 28 > /sys/class/gpio/export
echo out > /sys/class/gpio/gpio38/direction
echo out > /sys/class/gpio/gpio28/direction
# led 컨트롤러 초기화 끝	

#호스트이름 변경 시작
#3echo "Verify current host name."
#for OLDHOSTNAME in `hostname`
#do
#
#echo `basename $OLDHOSTNAME`
#done
#if [ $OLDHOSTNAME = $scanhostname ]
#       then
#               echo "The current hostname is the same as the config.txt name.."
#       else
#               echo $scanhostname > /etc/hostname
#               echo "The current host name is different from the config.txt name Reboot after setting."
#               sed -i "1s/.*/$scanhostname/g" /etc/hostname
#               sed -i "2s/.*/127.0.1.1 $scanhostname/g" /etc/hosts
#               reboot
#       fi
#호스트이름 변경 끝

# artik 부팅 중 무선 드라이버가 올라오기까지 4초간 대기
sleep 4 

# 무선 설정 시작.
if [ -e /scanset/wpa_psk.conf ]
        then
                if [ -e /scanset/wifi ]
                        then
                                rm /scanset/wpa_psk.conf
                                rm /scanset/wifi
                                reboot
        		else
		        touch /scanset/wifi
		fi
# 무선 설정 파일이 있는경우 클라이언트 모드로 무선 연결시작
                echo "The wireless configuration file is verified "
                echo "Enable wlan0"
                systemctl stop connman
                ifconfig wlan0 up
                echo "wlan0 connection."
                wpa_supplicant -B -iwlan0 -c /scanset/wpa_psk.conf -Dwext
                echo "dhclient wlan0..."
                dhclient wlan0
                ifconfig
                echo 1 > /sys/class/gpio/gpio38/value
                rm /scanset/wifi
# 무선 설정 파일이 있는경우 클라이언트 모드로 무선 연결 끝 

# 업데이트 자동 다운로드 시작 
#                cd /
#                updatec=$(ls /update -d)
#                if [ "$updatec" == "/update"  ]
#                then
#                echo "/update directory Verified"
#                else
#                echo "Create /update directory"
#                mkdir /update
#                fi
#                cd /update
#
#                echo "Check the war version of the server"
#                wget -r ftp://artik:appleartik@aeoneight.com/warver.txt
#                cb=$(cat Current.txt)
#                sb=$(cat aeoneight.com/warver.txt)
#                echo "Version of the server $sb"
#                echo "Current version $cb"
#                        if [ $cb == $sb ]
#                                then
#                                        echo "Same as server version"
#                                        cd /
#                                else
#                                        echo "Update to version of server"
#                                        sbdr=$(cut -b1-8 aeoneight.com/warver.txt)
#                                        echo "Stop Tomcat8"
#                                        service tomcat8 stop
#                                        echo "Download War"
#                                      wget -r ftp://artik:appleartik@aeoneight.com/war/$sbdr/*
#                                        echo "Update War"
#                                        cd aeoneight.com/war/$sbdr
#                                        echo "Run additional scripts"
#                                        source up.txt
#                                        findwar=$(ls *.war)
#                                        cp $findwar /var/lib/tomcat8/webapps/ROOT
#                                        cd /var/lib/tomcat8/webapps/ROOT
#                                        jar xvf $findwar
#                                        sync
#                                        cd /update
#                                     cp aeoneight.com/warver.txt Current.txt
#                                      echo "War update complete"
#                                       echo "Start Tomcat8"
#                                        service tomcat8 start
#                                        cd /
#                                        echo 1 > /sys/class/gpio/gpio38/value
#                       fi
# 업데이트 자동 다운로드 끝
        else

# 무선 설정 파일이 없는 경우 AP 모드로 연결 시작
                echo "The wireless configuration file is not verified."
                echo "Set to AP mode."
                echo "system check."
                lshw -C system > system.txt
                sleep 1
                boardsn=$(cat system.txt | sed 's/ //g' | grep serial | cut -d":" -f2 | cut -c 4-)

                echo "AP mode change."
                systemctl stop connman
                sed -i "3s/.*/ssid=voim-$boardsn/g" /scanset/hostapd.conf
                ifconfig wlan0 192.168.1.1 up
                sleep 1
                dnsmasq -C /etc/dnsmasq.conf
                sysctl net.ipv4.ip_forward=1
                iptables --flush
                iptables -t nat --flush
                iptables --delete-chain
                iptables -t nat --delete-chain
                iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
                iptables -A FORWARD -i wlan0 -j ACCEPT
                hostapd /scanset/hostapd.conf -B
                echo 1 > /sys/class/gpio/gpio28/value
# 무선 설정 파일이 없는 경우 AP 모드로 연결 끝

fi
ifconfig
cd /scanset
echo "wssvc start."
./wssvc &


