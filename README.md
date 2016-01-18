# urlshort-scala

Main point to note is: use maven-shade-plugin to build a all-included jar, since maven-assembly-plugin simply overwrites files with mathcing names, while maven-shade-plugin may be made to merge them. This is especially important for 'reference.conf' files.

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
