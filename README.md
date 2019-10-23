# codeimage

A visiualizer for source files in a [git](https://git-scm.org) repository.

## Installation

```shell
$ git clone https://github.com/leeaustinadams/codeimage
```

## Usage

Takes the following arguments: `path-to-repo path-to-file [output-file]`  

- Required:  
  `path-to-repo`  
  `path-to-file`  
- Optional:  
  `output-file` filename to write as PNG  

    $ java -jar codeimage-0.1.0-standalone.jar [args]

## Examples

```shell
$ java -jar codeimage-0.1.0-standalone.jar . src/codeimage/core.clj
$ java -jar codeimage-0.1.0-standalone.jar . src/codeimage/core.clj core.png
```

## Bugs

Yeah, probably...

## License

Copyright © 2019 Lee Adams

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
