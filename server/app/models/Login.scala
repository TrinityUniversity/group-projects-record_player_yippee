package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future
import org.mindrot.jbcrypt.BCrypt
import java.sql.Date

class LoginModel(db: Database)(implicit ec: ExecutionContext){
    def validateUser(username: String, password: String) : Future[Option[Int]] = {
        db.run(Users.filter(userRow => userRow.username === username).result)
            .map{userRows => userRows.headOption.flatMap{
                userRow => if (BCrypt.checkpw(password,userRow.password)) Some(userRow.id) else None
            }
        }
    }

    def createUser(username:String,password: String): Future[Option[Int]] = {
        db.run(Users.filter(userRow => userRow.username === username).result)
            .flatMap{userRows =>
                if(!userRows.nonEmpty){
                    db.run(Users += UsersRow(-1,username,"",BCrypt.hashpw(password,BCrypt.gensalt()),Date.valueOf(java.time.LocalDate.now())))
                        .flatMap{addCount => 
                            if(addCount > 0) db.run(Users.filter(userRow => userRow.username === username).result)
                                .map(_.headOption.map(_.id))
                            else Future.successful(None)
                        }
                } else {
                    Future.successful(None)
                }
            }
        
    }

}