# urlshort-scala

Trying to make the project from [here](http://grasswire-engineering.tumblr.com/post/94043813041/a-url-shortener-service-in-45-lines-of-scala) work. 

Main goals (at the time of writing): 
* learn to write and configure scala apps using maven
* using redis (here [scredis 1.1.2](https://github.com/Livestream/scredis/wiki/Home-1.x.x) is used)
* async programming in scala

Here scredis uses all default configuration from [`reference.conf`](https://github.com/Livestream/scredis/wiki/Configuration-1.x.x).

Main point to note is: use maven-shade-plugin to build a all-included jar, since maven-assembly-plugin simply overwrites files with mathcing names, while maven-shade-plugin may be made to merge them (see [akka docs](http://doc.akka.io/docs/akka/current/general/configuration.html#When_using_JarJar__OneJar__Assembly_or_any_jar-bundler)). This is especially important for 'reference.conf' files.

Maven archetype `net.alchim31.maven:scala-archetype-simple` is used to initialize the project (see [this scala tutorial](http://docs.scala-lang.org/tutorials/scala-with-maven.html)). 

Now, once built and lauched, one can call the app like this (using Powershell): 

```
PS E:\Users\sa1nt> Invoke-RestMethod -Uri 127.0.0.1:8080/http%3A%2F%2Fgoogle.com -Method Post
http://myshortdomain.com/9dLEZWF
```

Which adds a KV pair to Redis:

```
> redis-cli.exe
127.0.0.1:6379> GET 9dLEZWF
"http://google.com"
127.0.0.1:6379>
```
