package main

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"io/ioutil"
	"net/url"
	"strings"
	"time"
)

func main() {}

// 增加logger
var logger Logger = nil

func init() {
	fmt.Println(string(ModifierRegisterer), " @@@ loaded!!!")
}

// ModifierRegisterer is the symbol the plugin loader will be looking for. It must
// implement the plugin.Registerer interface
// https://github.com/luraproject/lura/blob/master/proxy/plugin/modifier.go#L71
var ModifierRegisterer = registerer("krakend-debugger")

type registerer string

type Logger interface {
	Debug(v ...interface{})
	Info(v ...interface{})
	Warning(v ...interface{})
	Error(v ...interface{})
	Critical(v ...interface{})
	Fatal(v ...interface{})
}

func (registerer) RegisterLogger(in interface{}) {
	l, ok := in.(Logger)
	if !ok {
		return
	}
	logger = l
	logger.Debug(fmt.Sprintf("[###PLUGIN: %s] Logger loaded", ModifierRegisterer))

}

// RegisterModifiers is the function the plugin loader will call to register the
// modifier(s) contained in the plugin using the function passed as argument.
// f will register the factoryFunc under the name and mark it as a request
// and/or response modifier.
func (r registerer) RegisterModifiers(f func(
	name string,
	factoryFunc func(map[string]interface{}) func(interface{}) (interface{}, error),
	appliesToRequest bool,
	appliesToResponse bool,
)) {
	f(string(r)+"-request", r.requestDump, true, false)
	f(string(r)+"-response", r.responseDump, false, true)
	fmt.Println(string(r), " ### registered!!!")
}

// RequestWrapper is an interface for passing proxy request between the krakend pipe
// and the loaded plugins
type RequestWrapper interface {
	Params() map[string]string
	Headers() map[string][]string
	Body() io.ReadCloser
	Method() string
	URL() *url.URL
	Query() url.Values
	Path() string
}

// ResponseWrapper is an interface for passing proxy response between the krakend pipe
// and the loaded plugins
type ResponseWrapper interface {
	Data() map[string]interface{}
	Io() io.Reader
	IsComplete() bool
	StatusCode() int
	Headers() map[string][]string
}

var unkownTypeErr = errors.New("unknow request type")

func (r registerer) requestDump(
	cfg map[string]interface{},
) func(interface{}) (interface{}, error) {
	// check the cfg. If the modifier requires some configuration,
	// it should be under the name of the plugin.
	// ex: if this modifier required some A and B config params
	/*
	   "extra_config":{
	       "plugin/req-resp-modifier":{
	           "name":["krakend-debugger"],
	           "krakend-debugger":{
	               "A":"foo",
	               "B":42
	           }
	       }
	   }
	*/

	// return the modifier
	fmt.Println("request dumper injected!!!")
	logger.Debug(fmt.Sprintf("[PLUGIN: %s] Request modifier injected", ModifierRegisterer))
	return func(input interface{}) (interface{}, error) {
		req, ok := input.(RequestWrapper)
		if !ok {
			return nil, unkownTypeErr
		}

		buf := new(bytes.Buffer)
		buf.ReadFrom(req.Body()) // 將input 物件寫到緩衝物件，因此input body 內目前就沒東西了
		str := buf.String()      // 重緩衝物件取出字串內容

		new_req := modifierReq(req, str) // 然後呼叫 modifier方法重新實現一個requestWrapper 物件

		fmt.Println("Req params:", req.Params())
		fmt.Println("headers:", req.Headers())
		fmt.Println("method:", req.Method())
		fmt.Println("url:", req.URL())
		fmt.Println("query:", req.Query())
		fmt.Println("path:", req.Path())
		fmt.Println("body:", str)

		// 轉json 物件輸出 要輸出json 物件屬性名稱需要大寫開頭
		//	fmt.Printf("%s\n",data)
		now := time.Now()
		re := Req{
			Timestamp: now.Unix(),
			Params:    req.Params(),
			Headers:   req.Headers(),
			Body:      str,
			Method:    req.Method(),
			Url:       req.URL(),
			Query:     req.Query(),
			Path:      req.Path(),
		}
		b, _ := json.Marshal(re)
		fmt.Println("@@@RequestJson:", string(b))
		//fmt.Println("@@@Request_RE:", re)

		//	c, _ := json.Marshal(req)
		//        fmt.Println("###:",string(c))

		return new_req, nil // 最後返回新重現的rw物件，不然input物件的body遺失後送會有問題
	}
}

func (r registerer) responseDump(
	cfg map[string]interface{},
) func(interface{}) (interface{}, error) {
	// check the cfg. If the modifier requires some configuration,
	// it should be under the name of the plugin.
	// ex: if this modifier required some A and B config params
	/*
	   "extra_config":{
	       "plugin/req-resp-modifier":{
	           "name":["krakend-debugger"],
	           "krakend-debugger":{
	               "A":"foo",
	               "B":42
	           }
	       }
	   }
	*/

	// return the modifier
	fmt.Println("response dumper injected!!!")
	logger.Debug(fmt.Sprintf("[PLUGIN: %s] Response modifier injected", ModifierRegisterer))
	return func(input interface{}) (interface{}, error) {
		resp, ok := input.(ResponseWrapper)
		if !ok {
			return nil, unkownTypeErr
		}

		fmt.Println("resp: statusCode", resp.StatusCode())

		buf := new(bytes.Buffer)
		buf.ReadFrom(resp.Io())             // 將input 物件寫到緩衝物件，因此input body 內目前就沒東西了
		str := buf.String()                 // 重緩衝物件取出字串內容
		new_resp := modifierResp(resp, str) // 然後呼叫 modifier方法重新實現一個requestWrapper 物件

		now := time.Now()

		re := Resp{ // 要輸出json 字首要大寫
			Timestamp:  now.Unix(),
			Data:       resp.Data(),
			Io:         str,
			IsComplete: resp.IsComplete(),
			Headers:    resp.Headers(),
			StatusCode: resp.StatusCode(),
		}

		//re := User{Name:"ic"}

		b, _ := json.Marshal(re)
		//fmt.Println("##Response_re:", re)
		fmt.Println("##ResponseJson:", string(b))

		return new_resp, nil
	}
}

//type User struct {
//  Name string
//}

func modifierReq(req RequestWrapper, str string) requestWrapper {
	return requestWrapper{
		params:  req.Params(),
		headers: req.Headers(),
		body:    ioutil.NopCloser(strings.NewReader(str)), // 將body內容重新讀給body參數
		method:  req.Method(),
		url:     req.URL(),
		query:   req.Query(),
		path:    req.Path(),
	}
}

func modifierResp(resp ResponseWrapper, str string) responseWrapper {
	return responseWrapper{
		data:       resp.Data(),
		io:         strings.NewReader(str), // 將body內容重新讀給body參數
		isComplete: resp.IsComplete(),
		statusCode: resp.StatusCode(),
		headers:    resp.Headers(),
	}
}

type responseWrapper struct {
	data       map[string]interface{}
	io         io.Reader
	isComplete bool
	statusCode int
	headers    map[string][]string
}

type requestWrapper struct {
	method  string
	url     *url.URL
	query   url.Values
	path    string
	body    io.ReadCloser
	params  map[string]string
	headers map[string][]string
}

func (r requestWrapper) Method() string               { return r.method }
func (r requestWrapper) URL() *url.URL                { return r.url }
func (r requestWrapper) Query() url.Values            { return r.query }
func (r requestWrapper) Path() string                 { return r.path }
func (r requestWrapper) Body() io.ReadCloser          { return r.body }
func (r requestWrapper) Params() map[string]string    { return r.params }
func (r requestWrapper) Headers() map[string][]string { return r.headers }

func (r responseWrapper) Data() map[string]interface{} { return r.data }
func (r responseWrapper) Io() io.Reader                { return r.io }
func (r responseWrapper) IsComplete() bool             { return r.isComplete }
func (r responseWrapper) StatusCode() int              { return r.statusCode }
func (r responseWrapper) Headers() map[string][]string { return r.headers }

type Req struct {
	Timestamp int64
	Params    map[string]string
	Headers   map[string][]string
	Body      string
	Method    string
	Url       *url.URL
	Query     url.Values
	Path      string
}

type Resp struct {
	Timestamp  int64
	Data       map[string]interface{}
	Io         string
	IsComplete bool
	StatusCode int
	Headers    map[string][]string
}
