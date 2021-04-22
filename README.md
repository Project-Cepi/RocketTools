# RocketTools
Extension manager for Minestom

## Hooking your Extension

First, make sure your unload functions
unload _everything_ that extension has done,
including commands, chunks, etc.
If some side effects cant be unloaded,
make sure that your extension can load
even if those side effects are still present.

After that, set a property in `meta`'s `downloadURL`
to something that points to your latest jar.

For example, in RocketTools itself:

```
  "meta": {
    "downloadURL": "https://github.com/Project-Cepi/RocketTools/releases/download/latest/rocket-all.jar"
  }
```