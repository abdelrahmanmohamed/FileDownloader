name := "FileDownloader"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.22"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.22"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "junit" % "junit" % "4.12" % "test"

libraryDependencies += "junit-addons" % "junit-addons" % "1.4" % "test"

libraryDependencies += "org.apache.sshd" % "sshd-core" % "1.3.0"

parallelExecution in ThisBuild := false
