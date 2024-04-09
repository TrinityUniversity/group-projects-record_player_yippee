package models

object CodeGen extends App {
    slick.codegen.SourceCodeGenerator.run(
        "slick.jdbc.PostgresProfile",
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost/recordPlayer?user=gchollet&password=password",
        "C:/Users/garre/Documents/CS/School/Junior Year/Web Apps/group-projects-record_player_yippee/server/app",
        "models", None, None, true, false
    )
}