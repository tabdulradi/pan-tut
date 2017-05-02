tutSettings

lazy val pandoc = taskKey[Unit]("Run pandoc")

pandoc :=
  println(tutTargetDirectory.value.listFiles().map { f =>
    s"""pandoc $f -o $f.html""".!!
  })

publishLocal := (publishLocal dependsOn pandoc dependsOn tut).value
