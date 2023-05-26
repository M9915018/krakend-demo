package main

import (
	"fmt"
	"net"
	"os"
	"strconv"
	"time"
)

var call_key_id string

func main() {
	//name := "Go Developers"
	//fmt.Println("Azure for", name)
	now_utc := time.Now().UTC()
	now := time.Now()
	// 塞鏈結ID到headers
	x := 10
	// host_service-start-time_now
	call_key_id = "ic_" + strconv.FormatInt(now.UnixNano(), x)
	fmt.Println("call_key_id:", call_key_id)
	fmt.Println("時差:", now_utc.UnixNano()-now.UnixNano())

	fmt.Println("GetHostname:", GetHostname())
	fmt.Println("GetIP:", GetIP())
}

func GetHostname() string {
	hostname, err := os.Hostname()
	if err != nil {
		fmt.Println("get hostname failed, err = ", err.Error())
		return ""
	}
	return hostname
}

func GetIP() string {
	ifaces, _ := net.Interfaces()
	var ip net.IP
	// handle err
	for _, i := range ifaces {
		addrs, _ := i.Addrs()
		// handle err
		for _, addr := range addrs {
			// var ip net.IP
			switch v := addr.(type) {
			case *net.IPNet:
				ip = v.IP
			case *net.IPAddr:
				ip = v.IP
			}
			// fmt.Println("get ip", ip)
		}
	}
	return ip.String()
}
