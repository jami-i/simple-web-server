
name := "simple-web-server"

version := "1.0"

libraryDependencies += "com.twitter" %% "finagle-http" % "6.22.0"

mainClass in assembly := Some("com.github.jamii.sws.Main")