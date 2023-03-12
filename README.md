# MyPortfolio
## Summary

누구나 소통할수 있는 온라인 카페 API 를 만들어 보았습니다. 
회원가입후 자신이 원하는 게시판에 자유롭게 글과 댓글을 작성할수 있습니다.
마음에 드는 글에 좋아요를 누를수 있습니다. 

## 프로젝트 주요 관심사

 - SOLID 원칙을 준수하면서 모든 코드에 이유와 근거가 명확하도록 했습니다.
 - 코드가 직관적으로 이해될수 있도록 주석 다는것을 신경쓰었습니다.
 - 발생 할 수 있는 다양한 예외 상황을 정확하게 캐치 하였습니다.
 - 발생된 예외에 대한 알맞는 응답값을 명확하게 설정하였습니다. 

## 사용된 기술 스택
  - Java 11
  - Spring Boot, Spring Data Jpa, Spring Security
  - QueryDsl, Jpa, Postgresql
  - Gradle, Junit5

#### Spring Security
 - Ajax 통신 방식으로 로그인을 구현하였습니다.
 - 로그인 성공시 세션 / 쿠키 방식을 사용 하여 로그인 상태가 지속되도록 했습니다.
 - 인증되지 않은 사용자가 접속할수 있는 지원과 없는 자원을 명확하게 구분하여 보안 안정성을 높혔습니다.

### Spring Data Jpa, QueryDsl
 - SQL문을 별도로 작성하지 않고 DB를 객체 지향적으로 설계할수 있도록 해당 두 기술들을 사용하였습니다.

### Junit5
 - 발생할수 있는 다양한 예외 상황과 그 예외에 알맞는 응답값이 전송될수 있도록 테스트 케이스를 작성하였습니다.


## ERD
![image](https://user-images.githubusercontent.com/99637164/224530579-53a446fb-ccf0-440a-8b0a-a84606aa94ce.png)

## API 설계 및 기능 구현

### 회원가입 / 로그인 및 환경설정
| Feature | Request | API 
| --- | --- | -- | 
| 회원가입 | POST | /join 
| 로그인  | POST | /api/login 
| 로그아웃 | POST | /logout
| 비밀번호 변경 | POST |  /settings/password
| 내 정보 조회 | POST | /settings/profile 
| 회원 탈퇴  | DELETE | /settings/unregister   

### 관리자 기능 
| Feature | Request | API | 설명
| --- | --- | -- | ---
| 게시판 생성 | POST | /admin/board | 
| 회원정보 목록 조회  | GET | /admin/members?page= |
| 회원정보 조회 | GET | /admin/members/{username} |
| 글 삭제 | DELETE |  /admin/posts?id= |
| 댓글 삭제 | DELETE | /admin/comments?id= |
| 회원 강퇴  | DELETE | /admin/members/{username} |


## Development

| Feature | Request | API | 설명 |
| 회원가입 | POST | /join | 123 | 

Want to contribute? Great!

Dillinger uses Gulp + Webpack for fast developing.
Make a change in your file and instantaneously see your updates!

Open your favorite Terminal and run these commands.

First Tab:

```sh
node app
```

Second Tab:

```sh
gulp watch
```

(optional) Third:

```sh
karma test
```

#### Building for source

For production release:

```sh
gulp build --prod
```

Generating pre-built zip archives for distribution:

```sh
gulp build dist --prod
```

## Docker

Dillinger is very easy to install and deploy in a Docker container.

By default, the Docker will expose port 8080, so change this within the
Dockerfile if necessary. When ready, simply use the Dockerfile to
build the image.

```sh
cd dillinger
docker build -t <youruser>/dillinger:${package.json.version} .
```

This will create the dillinger image and pull in the necessary dependencies.
Be sure to swap out `${package.json.version}` with the actual
version of Dillinger.

Once done, run the Docker image and map the port to whatever you wish on
your host. In this example, we simply map port 8000 of the host to
port 8080 of the Docker (or whatever port was exposed in the Dockerfile):

```sh
docker run -d -p 8000:8080 --restart=always --cap-add=SYS_ADMIN --name=dillinger <youruser>/dillinger:${package.json.version}
```

> Note: `--capt-add=SYS-ADMIN` is required for PDF rendering.

Verify the deployment by navigating to your server address in
your preferred browser.

```sh
127.0.0.1:8000
```

## License

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [dill]: <https://github.com/joemccann/dillinger>
   [git-repo-url]: <https://github.com/joemccann/dillinger.git>
   [john gruber]: <http://daringfireball.net>
   [df1]: <http://daringfireball.net/projects/markdown/>
   [markdown-it]: <https://github.com/markdown-it/markdown-it>
   [Ace Editor]: <http://ace.ajax.org>
   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [jQuery]: <http://jquery.com>
   [@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
   [express]: <http://expressjs.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

   [PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
   [PlGh]: <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
   [PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
   [PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>
   [PlMe]: <https://github.com/joemccann/dillinger/tree/master/plugins/medium/README.md>
   [PlGa]: <https://github.com/RahulHP/dillinger/blob/master/plugins/googleanalytics/README.md>

