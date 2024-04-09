package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt
import java.sql.Date

class LoginModel(db: Database)(implicit ec: ExecutionContext){
    def validateUser(username: String, password: String) : Future[Boolean] = {
        db.run(Users.filter(userRow => userRow.username === username).result)
            .map(userRows => userRows.filter(userRow => BCrypt.checkpw(password,userRow.password)).nonEmpty)
    }

    def createUser(username:String,password: String): Future[Boolean] = {
        db.run(Users.filter(userRow => userRow.username === username).result)
            .flatMap{userRows =>
                if(!userRows.nonEmpty){
                    db.run(Users += UsersRow(-1,username,"",BCrypt.hashpw(password,BCrypt.gensalt()),Date.valueOf(java.time.LocalDate.now())))
                        .map(addCount => addCount > 0)
                } else {
                    Future.successful(false)
                }
            }
        
    }

    def getUserData(userId:Int) : Seq[String] = {
        ???
    }
}