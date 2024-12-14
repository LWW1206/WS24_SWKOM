function sendHelloWorldRequest(){
    $.ajax({
        type: "GET",
        url: "http://localhost:8081/hello",
        success: function(response){
            $("#helloWorld").text("REST Server successfully returned: " + response);
        },
        error: function (response){
            $("#helloWorld").text("Failed to fetch data. Please try again.");
        }
    });
}