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

var headers map[string][]string = make(map[string][]string)

func main() {
	//name := "Go Developers"
	//fmt.Println("Azure for", name)
	//now_utc := time.Now().UTC()
	now := time.Now()
	// 塞鏈結ID到headers
	x := 10
	// host_service-start-time_now
	fmt.Println("timestamp_str UnixNano():", strconv.FormatInt(now.UnixNano(), x))

	fmt.Println("timestamp_str Unix():", strconv.FormatInt(now.UnixMicro(), x))

	timestamp_str = strconv.FormatInt(now.UnixMicro(), x)

	fmt.Println("GetHostname:", GetHostname())
	fmt.Println("GetIP:", GetIP())
	fmt.Println("===================================================")
	// log 要包含這些東西 SpanID 處理程序的ID可以想成是被調用的應用
	// ParentSpanID 代表上一層調用本層的應用如果沒有填-1 代表最上層的應用
	// TransactionID 代表整個調用練的ID，相同的TransactionID代表是同一個調用鏈中的的相關請求，再透過ParentSpanID和SpanID區分層級結構
	fmt.Println("parentSpanId:", -1)
	spanID = GetSpanID()

	fmt.Println("Get64RandomNumber:", generateRandomString(64))
	fmt.Println("spanId:", spanID)
	fmt.Println("traceId:" + getTraceIdString(spanID, timestamp_str))

	setHeader(headers, now, parentSpanId)
	getHeaderData(headers, "KRAKEND_PARENT_SPAN_ID")
	//fmt.Println("parentSpanId:", parentSpanId)
}

var parentSpanId string = "-1"

func getHeaderData(headers map[string][]string, key string) {
	fmt.Print("headers[key]:", key+" :")
	value, exists := headers[key]
	if exists {
		fmt.Println(value)
		parentSpanId = value[0]
	} else {
		parentSpanId = "-1"
	}
}

func setHeader(headers map[string][]string, now time.Time, parentSpanId string) {

	spanID = GetSpanID()
	// 如果header 沒有這個字段就是rootpath 設定為-1
	//var parentSpanId = "-1"
	//fmt.Println("parentSpanId:", parentSpanId)
	//fmt.Println("spanId:", spanID)
	timestamp_str = strconv.FormatInt(now.UnixNano(), 10)
	var traceId = getTraceIdString(spanID, timestamp_str)

	header_traceId := []string{traceId}
	header_spanId := []string{spanID}
	header_parentSpanId := []string{parentSpanId}
	headers["KRAKEND_TX_ID"] = header_traceId
	headers["KRAKEND_SPAN_ID"] = header_spanId
	headers["KRAKEND_PARENT_SPAN_ID"] = header_parentSpanId

}

func getTraceIdString(spanID string, timestamp_str string) string { // 長度不能超過32 所以各16位元
	return spanID + timestamp_str
}

func GetSpanID() string {
	pid := GetPID()
	length := 16 - len(pid) //   length of pid
	return GetPID() + generateRandomString(length)
}

func GetPID() string {
	pid := os.Getpid()
	//fmt.Println("pid:", pid)
	return strconv.Itoa(pid)
}

func GetRandomNumber(number int) string {
	rand.Seed(time.Now().UnixNano())
	randomNumber := rand.Uint64()
	return strconv.FormatUint(randomNumber, 10)
}

func generateRandomString(length int) string {
	//var letters = []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
	var letters = []rune("0123456789")
	b := make([]rune, length)
	for i := range b {
		b[i] = letters[rand.Intn(len(letters))]
	}
	return string(b)
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
