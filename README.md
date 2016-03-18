#Build
```shell
 mvn clean install
```
#Run
Before running you should start mongo, create database "notesDB" and save your user login/pass in collection "user":
```
use notesDB
db.user.insert({"_id": "56c4411285eff2f03ec6b347","userName":"xyz","password":"xyz", "accessToken": ""})
```
Get access token for registered user:
```
curl -H "Content-Type: application/json" -X POST -d '{"userName":"xyz","password":"xyz"}' http://localhost:3000/api/login
```
Add note:
```
curl -H "Content-Type: application/json" -X PUT -d '{"title": "note 1", "body": "some text"}' http://localhost:3000/api/note?accessToken=some-uuid
```
Note list:
```
curl -X GET http://localhost:3000/api/note?accessToken=some-uuid
```
Get note by id:
```
curl -X GET http://localhost:3000/api/note/note-uuid?accessToken=some-uuid
```
Delete note:
```
curl -X DELETE http://localhost:3000/api/note/note-uuid?accessToken=some-uuid
```
Update note:
```
curl -H "Content-Type: application/json" -X POST -d '{"title": "note 1", "body": "some text"}' http://localhost:3000/api/note/note-uuid?accessToken=some-uuid
```

