package com.abc.com.abc.core.datamining.demo
import scala.reflect.runtime.universe._

/**
  * 泛型对象在运行的时候，它的T是被擦除的。ClassTag[T]存储的是给定类型的T，我们通过runtimeClass来访问
  * 这个泛型在运行时指定的对象。在实例化Array的时候，这个特别有用。在构建数组但是它的元素类型不知道
  * 的时候。在编译时是数组的元素类型是不知道的(运行时知道)。
  *
  * ClassTag是比TypeTag更弱的一种情况。ClassTag只包含了运行时给定的类的类别信息。而TypeTag不仅包含类
  * 的类别信息，还包含了所有静态的类信息。我们在绝大多数情况下会使用ClassTag，因为ClassTag告诉我们运
  * 行时实际的类型已经足够我们在做泛型的时候去使用了。
  *
  * 数组本身是泛型。而如果我们想创建一个泛型数组的话，理论上是不可以的。
  * 在Scala中运行时，数组必须要有具体的类型，如果你继续是泛型的话，会提示你
  * 具体的类型没有，无法创建相应的数组，这是个很大的问题！
  * 在Scala中引入了ClassTag。有了它，我们就可以创建一个泛型数组。
  *
  * 我们要构建泛型对象，这里是泛型数组。我们需要ClassTag来帮我们存储T的实际的类型。
  * 在运行时我们就能获取这个实际的类型。
  *
  * Abstract class for parameter case classes.
  * This overrides the [[toString]] method to print all case class fields by name and value.
  *
  * @tparam T  Concrete parameter class.
  */
abstract class AbstractParams[T: TypeTag] {

  val PATH_NAME : String = "D:\\DevN\\sample-data\\spark-data\\mllib\\"

  private def tag: TypeTag[T] = typeTag[T]

  /**
    * Finds all case class fields in concrete class instance, and outputs them in JSON-style format:
    * {
    *   [field name]:\t[field value]\n
    *   [field name]:\t[field value]\n
    *   ...
    * }
    */
  override def toString: String = {
    val tpe = tag.tpe
    val allAccessors = tpe.declarations.collect {
      case m: MethodSymbol if m.isCaseAccessor => m
    }
    val mirror = runtimeMirror(getClass.getClassLoader)
    val instanceMirror = mirror.reflect(this)
    allAccessors.map { f =>
      val paramName = f.name.toString
      val fieldMirror = instanceMirror.reflectField(f)
      val paramValue = fieldMirror.get
      s"  $paramName:\t$paramValue"
    }.mkString("{\n", ",\n", "\n}")
  }
}
