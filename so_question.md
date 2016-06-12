# Clojure macro scoping problem

Hey S/O,
I'm relatively new to Clojure, but i think it's an incredibly beautiful language even though it's been giving me a lot of pain lately. I've been stuck on this Clojure macro problem for days now, and I've read every guide on the internet about how to do macros in Clojure, and tried rewriting my code several times but I just can't figure it out.

let's say I have a function that I need to call with a list of keyword arguments that is generated dynamically. The first part of the function call is known `(println "a")` and the second part of the form is generated dynamically `["b" "c"]` My understanding is that the correct way to achieve this is with a macro that looks something like this:

```
(defmacro append-extra-args [partial-form extra-args]
  `(~@partial-form ~@extra-args))
(macroexpand '(append-extra-args (println "a") ["b" "c"]))
; => (println "a" "b" "c")
```

Now that's all well and good but what if extra-args was provided by a local variable?

```
(def my-extra-args ["b" "c"])
(defmacro append-extra-args [partial-form extra-args]
  `(~@partial-form ~@extra-args))
(macroexpand '(append-extra-args (println "a") my-extra-args))
```

Now the macro doesn't work because it can't expand the symbol `extra-args` from within the macro. It seems like the only way around this is to evaluate `extra-args` in the macro itself

```
(def my-extra-args ["b" "c"])
(defmacro append-extra-args [partial-form extra-args-name]
  (let [extra-args# (eval extra-args-name)]
    `(~@partial-form ~@extra-args#)))
(macroexpand '(append-extra-args (println "a") my-extra-args))
```

This work, but it feels awfully clunky and it only works if the `extra-args` binding is reachable from the scope of the macro, which means this doesn't work:

```
(def foo-extra-args ["y" "z"])
(def bar-extra-args ["a" "b"])
(defn do-stuff-with-extra-args [extra-args]
  (println "adding extra args" extra-args)
  (append-extra-args (println "a") extra-args))
(do-stuff-with-extra-args bar-extra-args)
```

because I get the error "Can't eval locals".

So what's wrong here? I feel like I'm either missing something in my understanding of clojure macro best practices or how scoping works in clojure.

If you're wondering why I need to do this, here is the code I'm trying to fix
