curl --form "upload1=@path_to_xml_file" http://app_ipaddress:5320/upload.html

RESTFul post

curl -X POST -d @filename http://example.com/path/to/resource --header "Content-Type:text/xml"

curl -X POST -d @filename.txt http://example.com/path/to/resource --header "Content-Type:application/json"

curl -X POST -d @filename.txt http://example.com/path/to/resource --header "Content-Type:application/json"
curl -X POST -d '<iCeeNee ip="192.168.0.188" mode="hd"/>' 192.168.0.55:5230 --header "Content-Type:application/json"

curl -X POST -d '<iCeeNee ip="192.168.0.188" mode="hd"/>' 192.168.0.55:5230