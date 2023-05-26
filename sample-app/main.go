package main

import (
	"fmt"
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

}
