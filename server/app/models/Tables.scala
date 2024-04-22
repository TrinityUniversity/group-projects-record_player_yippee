package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends Tables {
  val profile = slick.jdbc.PostgresProfile
}

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = CollectionItems.schema ++ Collections.schema ++ Records.schema ++ Users.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table CollectionItems
   *  @param itemId Database column item_id SqlType(serial), AutoInc, PrimaryKey
   *  @param collectionId Database column collection_id SqlType(int4)
   *  @param recordId Database column record_id SqlType(int4) */
  case class CollectionItemsRow(itemId: Int, collectionId: Int, recordId: Int)
  /** GetResult implicit for fetching CollectionItemsRow objects using plain SQL queries */
  implicit def GetResultCollectionItemsRow(implicit e0: GR[Int]): GR[CollectionItemsRow] = GR{
    prs => import prs._
    CollectionItemsRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table collection_items. Objects of this class serve as prototypes for rows in queries. */
  class CollectionItems(_tableTag: Tag) extends profile.api.Table[CollectionItemsRow](_tableTag, "collection_items") {
    def * = (itemId, collectionId, recordId).<>(CollectionItemsRow.tupled, CollectionItemsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(itemId), Rep.Some(collectionId), Rep.Some(recordId))).shaped.<>({r=>import r._; _1.map(_=> CollectionItemsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column item_id SqlType(serial), AutoInc, PrimaryKey */
    val itemId: Rep[Int] = column[Int]("item_id", O.AutoInc, O.PrimaryKey)
    /** Database column collection_id SqlType(int4) */
    val collectionId: Rep[Int] = column[Int]("collection_id")
    /** Database column record_id SqlType(int4) */
    val recordId: Rep[Int] = column[Int]("record_id")

    /** Foreign key referencing Collections (database name collection_items_collection_id_fkey) */
    lazy val collectionsFk = foreignKey("collection_items_collection_id_fkey", collectionId, Collections)(r => r.collectionId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing Records (database name collection_items_record_id_fkey) */
    lazy val recordsFk = foreignKey("collection_items_record_id_fkey", recordId, Records)(r => r.recordId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table CollectionItems */
  lazy val CollectionItems = new TableQuery(tag => new CollectionItems(tag))

  /** Entity class storing rows of table Collections
   *  @param collectionId Database column collection_id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(50,true)
   *  @param userId Database column user_id SqlType(int4) */
  case class CollectionsRow(collectionId: Int, name: String, userId: Int)
  /** GetResult implicit for fetching CollectionsRow objects using plain SQL queries */
  implicit def GetResultCollectionsRow(implicit e0: GR[Int], e1: GR[String]): GR[CollectionsRow] = GR{
    prs => import prs._
    CollectionsRow.tupled((<<[Int], <<[String], <<[Int]))
  }
  /** Table description of table collections. Objects of this class serve as prototypes for rows in queries. */
  class Collections(_tableTag: Tag) extends profile.api.Table[CollectionsRow](_tableTag, "collections") {
    def * = (collectionId, name, userId).<>(CollectionsRow.tupled, CollectionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(collectionId), Rep.Some(name), Rep.Some(userId))).shaped.<>({r=>import r._; _1.map(_=> CollectionsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column collection_id SqlType(serial), AutoInc, PrimaryKey */
    val collectionId: Rep[Int] = column[Int]("collection_id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(50,true) */
    val name: Rep[String] = column[String]("name", O.Length(50,varying=true))
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")

    /** Foreign key referencing Users (database name collections_user_id_fkey) */
    lazy val usersFk = foreignKey("collections_user_id_fkey", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Collections */
  lazy val Collections = new TableQuery(tag => new Collections(tag))

  /** Entity class storing rows of table Records
   *  @param recordId Database column record_id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar)
   *  @param length Database column length SqlType(float8), Default(None)
   *  @param fileLocation Database column file_location SqlType(varchar)
   *  @param creatorId Database column creator_id SqlType(int4) 
   *  @param artist Database column artist SqlType(varchar)*/
  case class RecordsRow(recordId: Int, name: String, length: Option[Double] = None, fileLocation: String, creatorId: Int, artist: String)
  /** GetResult implicit for fetching RecordsRow objects using plain SQL queries */
  implicit def GetResultRecordsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Double]]): GR[RecordsRow] = GR{
    prs => import prs._
    RecordsRow.tupled((<<[Int], <<[String], <<?[Double], <<[String], <<[Int], <<[String]))
  }
  /** Table description of table records. Objects of this class serve as prototypes for rows in queries. */
  class Records(_tableTag: Tag) extends profile.api.Table[RecordsRow](_tableTag, "records") {
    def * = (recordId, name, length, fileLocation, creatorId, artist).<>(RecordsRow.tupled, RecordsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(recordId), Rep.Some(name), length, Rep.Some(fileLocation), Rep.Some(creatorId), Rep.Some(artist))).shaped.<>({r=>import r._; _1.map(_=> RecordsRow.tupled((_1.get, _2.get, _3, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column record_id SqlType(serial), AutoInc, PrimaryKey */
    val recordId: Rep[Int] = column[Int]("record_id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar) */
    val name: Rep[String] = column[String]("name")
    /** Database column length SqlType(float8), Default(None) */
    val length: Rep[Option[Double]] = column[Option[Double]]("length", O.Default(None))
    /** Database column file_location SqlType(varchar) */
    val fileLocation: Rep[String] = column[String]("file_location")
    /** Database column creator_id SqlType(int4) */
    val creatorId: Rep[Int] = column[Int]("creator_id")
    /** Database column artist SqlType(varchar) */
    val artist: Rep[String] = column[String]("artist")

    /** Foreign key referencing Users (database name records_creator_id_fkey) */
    lazy val usersFk = foreignKey("records_creator_id_fkey", creatorId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Records */
  lazy val Records = new TableQuery(tag => new Records(tag))

  /** Entity class storing rows of table Users
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param username Database column username SqlType(varchar), Length(25,true)
   *  @param email Database column email SqlType(varchar)
   *  @param password Database column password SqlType(varchar), Length(200,true)
   *  @param dateJoined Database column date_joined SqlType(date) */
  case class UsersRow(id: Int, username: String, email: String, password: String, dateJoined: java.sql.Date)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Date]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[java.sql.Date]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * = (id, username, email, password, dateJoined).<>(UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(username), Rep.Some(email), Rep.Some(password), Rep.Some(dateJoined))).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(25,true) */
    val username: Rep[String] = column[String]("username", O.Length(25,varying=true))
    /** Database column email SqlType(varchar) */
    val email: Rep[String] = column[String]("email")
    /** Database column password SqlType(varchar), Length(200,true) */
    val password: Rep[String] = column[String]("password", O.Length(200,varying=true))
    /** Database column date_joined SqlType(date) */
    val dateJoined: Rep[java.sql.Date] = column[java.sql.Date]("date_joined")
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
