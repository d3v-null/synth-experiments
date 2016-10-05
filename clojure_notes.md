Namespaces
====
if you have a repl going with a project and you want to change to a namespace within the same project then:
```
  (load "path_to/namespace")
  (in-ns 'namespace)
```

if that namespace `:require`s another namespace `:as` an alias e.g.

```
  (ns clojure-project.namespace
    (:require
      [external-project.namespace :as alias]))
```

then you can access things from the alias namespace using

```
  (alias/thing)
```
