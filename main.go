package main

import (
	"net/http"
	"time"
	"fmt"
	"flag"
	"github.com/antheagao/femProject/internal/routes"
	"github.com/antheagao/femProject/internal/app"
)

func main() {
	var port int
	flag.IntVar(&port, "port", 8080, "server port number")
	flag.Parse()

	app, err := app.NewApplication()

	if err != nil {
		panic(err)
	}

	defer app.DB.Close()

	r := routes.SetupRoutes(app)
	server := &http.Server{
		Addr: 				fmt.Sprintf(":%d", port),
		Handler:			r,
		IdleTimeout: 	time.Minute,
		ReadTimeout: 	10 * time.Second,
		WriteTimeout: 30 * time.Second,
	}

	app.Logger.Printf("We are running on port: %d\n", port)

	err = server.ListenAndServe()

	if err != nil {
		app.Logger.Fatal(err)
	}
}

