package main

import (
	"fmt"
	"math/rand"
	"net"
	"os"
	"strconv"
	"strings"
	"time"
)

var timestamp_str string
var spanID string

func main() {
	//name := "Go Developers"
	//fmt.Println("Azure for", name)
	now_utc := time.Now().UTC()
	now := time.Now()
	// 塞鏈結ID到headers
	x := 10
	// host_service-start-time_now
	timestamp_str = strconv.FormatInt(now.UnixNano(), x)
	fmt.Println("timestamp_str:", timestamp_str)
	fmt.Println("時差:", now_utc.UnixNano()-now.UnixNano())

	fmt.Println("GetHostname:", GetHostname())
	fmt.Println("GetIP:", GetIP())
	fmt.Println("===================================================")
	// log 要包含這些東西 SpanID 處理程序的ID可以想成是被調用的應用
	// ParentSpanID 代表上一層調用本層的應用如果沒有填-1 代表最上層的應用
	// TransactionID 代表整個調用練的ID，相同的TransactionID代表是同一個調用鏈中的的相關請求，再透過ParentSpanID和SpanID區分層級結構
	fmt.Println("parentSpanId:", -1)
	spanID = GetPID()
	GetPID()
	fmt.Println("Get64RandomNumber:", Get64RandomNumber())
	fmt.Println("spanId:", spanID)
	fmt.Println("traceId:" + GetHostname() + "|" + spanID + "|" + timestamp_str)
}

func GetPID() string {
	pid := os.Getpid()
	//fmt.Println("pid:", pid)
	return strconv.Itoa(pid)
}

func Get64RandomNumber() string {
	rand.Seed(time.Now().UnixNano())
	randomNumber := rand.Uint64()
	return strconv.FormatUint(randomNumber, 10)
}

func GetPIDFileName() string {
	fileName := os.Args[0]
	//fmt.Println("fileName:", fileName)
	return doReplace(fileName)
}

func doReplace(orgstr string) string {
	r := strings.NewReplacer("\\", "", ".", "", ":", "", "-", "")
	var n_str = r.Replace(orgstr)
	return n_str
}

func GetHostname() string {
	hostname, err := os.Hostname()
	if err != nil {
		fmt.Println("get hostname failed, err = ", err.Error())
		return ""
	}
	return doReplace(hostname)
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
