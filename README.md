# KeY4CATs -- A Deductive Verification Tool for Context-aware Trace Contracts

Requirements:
- java jdk 21
- gradle 8.5.0
- python3-matplotlib (for plots)

To build key4cats' .jar run (in the root folder `/key4cats`)

```    
    ./gradlew :cats:shadowjar
```

To execute KeY4CATs run
```
java -jar PATH_TO_THIS_FOLDER/key4cats/cats/build/libs/KeY4CATs.jar
```

For simplicity, in our examples we use the abbreviation `key4cats`, which can be defined for Bash as follows (execute the following in the root folder `key4cats/`

- Enter KeY4CATs' folder
	```
 	cd key4cats/
 	```
- Copy the definition of `key4cats` to the `~/` folder 
	```
	cp .key4cats_setting_bash ~/.key4cats_setting_bash 
	```
- Add the definition of the env var `KEY4CATs` (mind the lowecase `s`)
	```
 	echo "export KEY4CATs=$(pwd)/cats/build/libs/KeY4CATs.jar" >> ~/.key4cats_setting_bash
 	```
- Make Bash load this setting for each new terminal
	```
 	echo "source ~/.key4cats_setting_bash" >> ~/.bashrc
 	source ~/.bashrc
 	```
For Zsh replace `~/.bashrc` with `~/.zshrc`.

To check that everyting is set properly run the following
 
```
	cd sanity-checks 
	source 1-check-env-var.sh 
	source 2-check-command.sh
	source 3-check-run-example.sh
```
No errors should occur, but only at most two warning, which are expected.


To see the message about the usage run 
```    
    key4cats --help
```

## Syntax of CATSL specification 
A CAT file (`.cats`) contains a list of CATs.

### Syntax of a Trace
```
    <trace>: 
        <state-fml> 
        | <observation> "." <trace>
        | <event>   
        | <abs-trace>
        | <trace> <trace-op> <trace>  
        ;
        
    <state-fml>: 
        "`" <predicate> "`" // `true` , `y=2`
        ;
        
    <observation>: 
        <observed-var>"::"<observing-var>  // x::y
        ;
        
    <event>:
        "start(" <method-name> "," <int> ")"    // start(m,0)
        | "pop(" <method-name> "," <int> ")"    // pop(m,0)
        | "ret(" <int> ")"                      // ret(0)
        ;
        
    <abs-trace>: 
        "~{" <method-name>";"...";"<method-name> "}~"  // ~~ , ~{m,m1,m2}~
        ; 
        
    <trace-op> : <and> | <or> | <chop> | <concat> ;
    <and> = "&" ;
    <or> = "|" ;
    <chop> = "**";
    <concat> = "." ;
        
```

### Syntax of a CAT

```

<cat>: <signature-cat> ":" <assumes-cl> ";" <ensures-cl> ";" <expects-cl> ";"

<signature-cat>: 
    "[" <cat-name> "]" "{" List(<cat-name>";") "}" <method-name>

<assumes-cl>: "requires:" <pre-trace> <chop> <pre-cond> ";"

<ensures-cl>: "ensures: [" <inner-trace> "]" <chop> <post-cond> ";"

<expects-cl>: "expects:" <post-trace> ";"
```
* `<cat-name>` (`String`): name of the CAT that is specified.
* `List(<cat-name>";")` (each cat name must be followed by ";"): list of name of CATs for called methods that are assumed to be valid (dependencies). These CATs must be specified in the same file. If the list is empty the braces can be omitted.
* `<method-name>` (`String`): name of the method to be verified.
* `<pre-trace>` (`<trace>`): pre-trace of this CAT.
* `<pre-cond>` (`<trace>`): pre-condition of this CAT.
* `<inner-trace>` (`<trace>`): inner-trace of this CAT for which the `start` and `pop` are implicit. 
* `<post-cond>` (`<trace>`):  post-condition, it should contain a list of observations followed a state formula.
* `<post-trace>` (`<trace>`): post-trace of this CAT.

### Example of a CAT

```
    [catOfM1] m1: ...
    [catOfM2] {} m2: ...
     
    [catOfM] {catOfM1;catOfM2;} m:
        requires: ~~ ** x::y `y=0`; 
        ensures: ~~ ** x::y1 `y=y1`;
        expects: ~~; 
```
1. `requires`: Anything allowed before m, x is equals to 0 in the pre-state.
2. `ensures`: Anything allowed in m, x in post-state has the same value as x in the pre-state.
3. `expects`: Anything allowed after m

**A missing dependency for a called procedure would make the symbolic execution stop**

The verification targets Java methods. It is required that 
- methods are declared as `public static void`
- methods can only have a boolean argument: it must be `true` when called 
- method calls must include the classname
- methods must contain a single `void` return, at the end of their declaration
- fields are defined as `public static int`, fields assignemnt do not have to refer to the classname
```
public class MyClass{
    public static int x;
    
    public static void removeOne(boolean sync){
        x = x - 1;
        return;
    }
    
    public static void removeTwo(boolean sync){
        MyClass.removeOne(true);
        MyClass.removeOne(true);
        return;
    }
}
``` 

[//]: # ()
[//]: # (# KeY -- Deductive Java Program Verifier)

[//]: # ()
[//]: # ([![Tests]&#40;https://github.com/KeYProject/key/actions/workflows/tests.yml/badge.svg&#41;]&#40;https://github.com/KeYProject/key/actions/workflows/tests.yml&#41; [![CodeQL]&#40;https://github.com/KeYProject/key/actions/workflows/codeql.yml/badge.svg&#41;]&#40;https://github.com/KeYProject/key/actions/workflows/codeql.yml&#41; [![CodeQuality]&#40;https://github.com/KeYProject/key/actions/workflows/code_quality.yml/badge.svg&#41;]&#40;https://github.com/KeYProject/key/actions/workflows/code_quality.yml&#41; )

[//]: # ()
[//]: # (This repository is the home of the interactive theorem prover KeY for formal verification and analysis of Java programs. KeY comes as a standalone GUI application, which allows you to verify the functional correctness of Java programs with respect to formal specifications formulated in the Java Modeling Language JML. Moreover, KeY can also be used as a library e.g. for symbolic program execution, first order reasoning, or test case generation.)

[//]: # ()
[//]: # (For more information, refer to)

[//]: # ()
[//]: # (* [The KeY homepage]&#40;https://key-project.org&#41; )

[//]: # (* [The KeY book]&#40;https://www.key-project.org/thebook2/&#41;)

[//]: # (* [The KeY developer documentation]&#40;https://keyproject.github.io/key-docs/&#41;)

[//]: # (* KeY's success stories:)

[//]: # (  * [Severe bug discovered in JDK sorting routine &#40;TimSort&#41;]&#40;http://www.envisage-project.eu/proving-android-java-and-python-sorting-algorithm-is-broken-and-how-to-fix-it/&#41;,  )

[//]: # (  * [Verification of `java.util.IdentityHashMap`]&#40;https://doi.org/10.1007/978-3-031-07727-2_4&#41;,)

[//]: # (  * [Google Award for analysing a bug in `LinkedList`]&#40;https://www.key-project.org/2023/07/23/cwi-researchers-win-google-award-for-finding-a-bug-in-javas-linkedlist-using-key/&#41;)

[//]: # ()
[//]: # (The current version of KeY is 2.12.0, licensed under GPL v2.)

[//]: # ()
[//]: # ()
[//]: # (Feel free to use the project templates to get started using KeY:)

[//]: # (* [For Verification Projects]&#40;https://github.com/KeYProject/verification-project-template&#41;)

[//]: # (* [Using as a Library]&#40;https://github.com/KeYProject/key-java-example&#41;)

[//]: # (* [Using as a Symbolic Execution Backend]&#40;https://github.com/KeYProject/key-symbex-example&#41;)

[//]: # ()
[//]: # (## Requirements)

[//]: # ()
[//]: # (* Hardware: >=2 GB RAM)

[//]: # (* Operating System: Linux/Unix, MacOSX, Windows)

[//]: # (* Java SE 11 or newer)

[//]: # (* Optionally, KeY can make use of the following binaries:)

[//]: # (  * SMT Solvers:)

[//]: # (    * [Z3]&#40;https://github.com/Z3Prover/z3#z3&#41;)

[//]: # (    * [cvc5]&#40;https://cvc5.github.io/&#41;)

[//]: # (    * [CVC4]&#40;https://cvc4.github.io/&#41;)

[//]: # (    * [Princess]&#40;http://www.philipp.ruemmer.org/princess.shtml&#41;)

[//]: # ()
[//]: # (## Content of the KeY folder)

[//]: # ()
[//]: # (This folder provides a [gradle]&#40;https://gradle.org&#41;-managed project following)

[//]: # ([Maven's standard folder layout]&#40;https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html&#41;.)

[//]: # (There are several subprojects in this folder. In general, every `key.*/` subproject contains a core component of KeY.)

[//]: # (Additional and optional components are in `keyext.*/` folders. The file `build.gradle` is the root build script)

[//]: # (describing the dependencies and common build tasks for all subprojects.)

[//]: # ()
[//]: # (`key.util`, `key.core` and `key.ui` are the base for the product "KeY Prover". Special care is needed)

[//]: # (if you plan to make changes here.)

[//]: # ()
[//]: # ()
[//]: # (## Compile and Run KeY)

[//]: # ()
[//]: # (Assuming you are in the directory of this README file, you can create a runnable and deployable version with one of these commands:)

[//]: # ()
[//]: # (1. With `./gradlew key.ui:run` you can run the user interface of KeY directly from the repository. )

[//]: # (   Use `./gradlew key.ui:run --args='--experimental'` to enable experimental features.)

[//]: # ()
[//]: # (2. Use `./gradlew classes` to compile KeY, which includes running JavaCC and Antlr.)

[//]: # (   Likewise, use `./gradlew testClasses` if you also want to compile the JUnit test classes.)

[//]: # ()
[//]: # (3. Test your installation with `./gradlew test`. Be aware that this will usually take multiple hours to complete.)

[//]: # (   With `./gradlew testFast`, you can run a more lightweight test suite that should complete in a few minutes.)

[//]: # ()
[//]: # (   You can select a specific test case with the `--tests` argument. Wildcards are allowed.)

[//]: # (   ```sh)

[//]: # (   ./gradlew :key.<subproject>:test --tests "<class>.<method>")

[//]: # (   ```)

[//]: # ()
[//]: # (   You can debug KeY by adding the `--debug-jvm` option, then attaching a debugger at `localhost:5005`.)

[//]: # ()
[//]: # (4. You can create a single jar-version, aka *fat jar*, of KeY with)

[//]: # (   ```sh)

[//]: # (   ./gradlew :key.ui:shadowJar)

[//]: # (   ```)

[//]: # (   The file is generated in `key.ui/build/libs/key-*-exe.jar`.)

[//]: # ()
[//]: # (5. A distribution is build with)

[//]: # (   ```sh)

[//]: # (   ./gradlew :key.ui:installDist :key.ui:distZip)

[//]: # (   ```)

[//]: # (   The distribution can be tested by calling `key.ui/install/key/bin/key.ui`)

[//]: # (   and is zipped in `key.ui/build/distributions`.)

[//]: # ()
[//]: # (   The distribution gives you potential of using single jar files.)

[//]: # ()
[//]: # (# Developing KeY)

[//]: # ()
[//]: # (* Quality is automatically assessed using [SonarQube]&#40;https://sonarqube.org&#41; on each pull request.)

[//]: # (  The results of the assessments &#40;pass/fail&#41; can be inspected in the checks section of the PR.)

[//]: # ()
[//]: # (  The rules and quality gate are maintained by Alexander Weigl)

[//]: # (  <weigl@kit.edu> currently.)

[//]: # ()
[//]: # (* More guideline and documentation for the KeY development can be found under)

[//]: # ([key-docs]&#40;https://keyproject.github.io/key-docs/devel/&#41;.)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (# Issues and Bug Reports)

[//]: # ()
[//]: # (* For bug reports, please use the [issue tracker]&#40;https://github.com/KeYProject/key/issues&#41; or send a mail to support@key-project.org. )

[//]: # ()
[//]: # (* For discussions, you may want to subscribe and use the mailing list <key-all@lists.informatik.kit.edu> or use [GitHub discussions]&#40;https://github.com/KeYProject/key/discussions&#41;.)

[//]: # ()
[//]: # (# Contributing to KeY)

[//]: # ()
[//]: # (Feel free to submit [pull requests]&#40;https://github.com/KeYProject/key/pulls&#41; via GitHub. Pull requests are assessed using automatic tests, formatting and static source checkers, as well as a manual review by one of the developers. More guidelines and documentation for the KeY development can be found under [key-docs]&#40;https://keyproject.github.io/key-docs/devel/&#41;.)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (# License Remark)

[//]: # ()
[//]: # (```)

[//]: # (This is the KeY project - Integrated Deductive Software Design)

[//]: # (Copyright &#40;C&#41; 2001-2011 Universität Karlsruhe, Germany)

[//]: # (						Universität Koblenz-Landau, Germany)

[//]: # (						and Chalmers University of Technology, Sweden)

[//]: # (Copyright &#40;C&#41; 2011-2023 Karlsruhe Institute of Technology, Germany)

[//]: # (						Technical University Darmstadt, Germany)

[//]: # (						Chalmers University of Technology, Sweden)

[//]: # ()
[//]: # (The KeY system is protected by the GNU General Public License.)

[//]: # (See LICENSE.TXT for details.)

[//]: # (```)
