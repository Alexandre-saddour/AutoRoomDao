## AutoRoomDao

### Is there any way to create a reusable generic base class DAOs with Android Room?
Some context about why I wrote this library [here](https://medium.com/@alex.saddour/android-room-automatic-dao-generation-e48661e83c98)

### Summary 
- Generate many basic database requests (less boilerplate)
- Auto threading (can be disabled as needed)
- Support `LIMIT` and `ORDER BY`
- Support `@Ignore`, `@ColumnInfo` and `@Embedded`
- Code generation is done at compile time
- Room "static query check" is preserved.
- Code generation is configurable
- Proguard removes functions that you don't use
- 100% kotlin

### Usage
We will consider this Entity (standard Room Entity, nothing special here).

```
@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var name: String,
        var age: Int,
        @Ignore var job: String
)
```

Replace `@Dao` by `@AutoDao` and specify the related entity
```
@AutoDao(User::class)
class UserDao
```

`getById` is automatically generated and you can write this.

```
getUserById(userId).subscribe()
```

No crash here, threading is done by default for `Single` and `Maybe` (using `Schedulers.io()` and `AndroidSchedulers.mainThread())`  

You can disable it if needed.

```
getUserById(userId, autoThread = false).subscribe()
```
 
`@AutoDao` automatically detect fields: let's try to use `name`  
```
getUserByName("Smith").subscribe()
```

Also `vaargs`
```
getUserByName("Joe", "William", "Jack").subscribe()
```

And `LIMIT`
```
getUserByName("Averell", limit = 10).subscribe()
```

Don't forget `ORDER BY`  

```
// Get all users and order them by name
getAllOrderedByName().subscribe()
  
// Get all users named "Joe" and order them by age, oldest first
getByNameOrderedByAge("Joe", order = Order.DESC).subscribe()
```

Only one depth of `ORDER BY` is generated, you cannot call something like `getByNameOrderedByAgeOrderedById`  

`@AutoDao` supports `@Ignore` annotation.  
Looking at User entity, `job` carries `@Ignore` annotation:  nothing is generated for this field  

```
getUserByJob().subscribe() // does not compile because of @Ignore
```

Of course native `@Insert`, `@Update` and `@Delete` are avaible too
```
delete(badUser).subscribe()
```

### Control code generation
You can control code generation with `@AutoDao` parameters
 
#### generateOrderBy
Control if "order by" related functions should be generated  
Default value: true

#### onInsertConflictStrategy and onUpdateConflictStrategy

As mentionned, AutoDao uses native annotations `@Insert` and `@Update`.  
You can control their conflict strategy by using `onInsertConflictStrategy` and `onUpdateConflictStrategy`  
Default value is `OnConflictStrategy.ABORT`


#### defaultRxReturnType
Room's Rx implementation support many types.  
Therefore, AutoDao generate each functions 3 times  

By default, the value of `defaultRxReturnType` is `Single`. Therefore `getById` will be generated as: 
- Single\<List\<User>> getById(id: Int)
- Maybe\<List\<User>> getByIdAsMaybe(id: Int)
- Flowable\<List\<User>> getByIdAsFlowable(id: Int)  

(notice that `getById` returns a `Single`)
  
If you set `defaultRxReturnType` to `Flowable`, then `getById` will be generated as
- Single\<List\<User>> getByIdAsSingle(id: Int)
- Maybe\<List\<User>> getByIdAsMaybe(id: Int)
- Flowable\<List\<User>> getByIdAs(id: Int) 

(notice that `getById` returns a `Flowable`)

   
#### generateOnlyDefaultRxReturnType

Control if AutoDao should generate each functions 3 times (for `Single`, `Maybe` and `Flowable`)
Couple of examples:

With `generateOnlyDefaultRxReturnType` to `true` and `defaultRxReturnType` to `Single`, functions will generated only with Single
```
Single<List<User>> getById(id: Int)
```
 
With `generateOnlyDefaultRxReturnType` to `true` and `defaultRxReturnType` to `Flowable`, functions will generated only with Flowable
```
Flowable<List<User>> getById(id: Int)
```

With `generateOnlyDefaultRxReturnType` to `false`, functions will be generated 3 times, changing `defaultRxReturnType` will then only affect names, ex with `Flowable`:
```
Single<List<User>> getByIdAsSingle(id: Int)
Maybe<List<User>> getByIdAsMaybe(id: Int)
Flowable<List<User>> getById(id: Int)
``` 

Default value is of `generateOnlyDefaultRxReturnType` is `false`



### Library Setup

#### 1- Gradle dependency + Module creation

Unfortunately, this library comes with a constraint.  

AutoRoomDao, generates a class annotated with `@Dao` and then Room will generate a second class.  
For this reason, we need AutoRoomDao to do its work before room does it own.  

The only solution I found so far is to put the model (annotated with `@Entity`) and the Dao (annotated with `@Autoroom`) in a separate module and link it as a dependency of the `app` module. Thus, we ensure that AutoRoomDao will be the first to do its code generation.  
This is an annoying constraint but not a blocking one for my projects.  

Basically it goes like this:  

a) `app` gradle files contains 
```
implementation project(":models") // this contains `User` and `UserDao`
```  

b) `models` gradle file contains  
```
kapt com.asaddour.autoroomdao:autoroomdao:0.7.3
compileOnly com.asaddour.autoroomdao:autoroomdao:0.7.3
compileOnly 'com.squareup:kotlinpoet:0.7.0'
```

If this is unclear, check the `sampleapp` and `dao` modules in this repository.

#### 2- Update your database class.

Annotating a class called `UserDao` with `@AutoDao` generates a second class called `Auto_UserDao`.  
You need to give `Auto_UserDao` to RoomDatabase.
```
@Database(entities = [User::class], version = 1)
abstract fun AppDatabase(): RoomDatabase {
  abstract fun users(): Auto_UserDao // Notice `Auto_UserDao` and not `UserDao`
}
```

### Limitations
Currently not supporting LiveData