# poppler-jni

JNI binding to the C++ interface of the
[Poppler](https://poppler.freedesktop.org) PDF library.

Not all Poppler API functions are implemented--mostly only what would be
needed to support an Image I/O plugin (that has not yet been developed). Pull
requests that expand the API coverage are welcome.

# Development

## Requirements

* Java 9+
* libpoppler
* gradle (for the Java stuff)
* cmake & make (to build the shared library)

## Making changes to native code

After adding, removing, or changing the signatures of any `native` Java
methods:

1. Invoke `generate_headers.sh`. This will regenerate the `.h` files in
   `src/main/cpp`.
2. For each of the header file function signatures that changed, update the
   corresponding function in the corresponding `.cpp` file.
3. Invoke `cmake .; make` to rebuild the shared library.


## 编译过程

~ 环境: centos 8

```shell
# 安装poppler-dev
$ dnf --enablerepo=powertools install poppler-cpp-devel
# 在当前项目的根目录下运行
$ cmake
$ make
# 编译结果是 /libpoppler-jni.so

# 编译java sdk 
$ mkdir tmp
$ javac src/main/java/edu/illinois/library/poppler/*.java \
    -d ./tmp \
    -h src/main/cpp
# 编写测试类
# 编译测试类
$ javac -cp ./tmp/ PopplerDocumentTest.java 

# 运行测试类
$ java -cp .:./tmp/ -Djava.library.path=/root/tmp/poppler-jni PopplerDocumentTest
```


> 测试类

```java
import java.nio.file.Files;
import java.nio.file.Path;
import edu.illinois.library.poppler.PopplerDocument;

class PopplerDocumentTest {

    public static void main(String[] args)throws Exception  {
        byte[] data = Files.readAllBytes(Path.of("~/tmp/poppler-jni/src/test/resources/pdf-multipage.pdf"));
        PopplerDocument instance = PopplerDocument.load(data);
        System.out.println(instance);
    }
}
```