<template lang="">
  <div>
    <div class="container-fluid " >
      <div class="row">
        <nav id="sidebar" class="col-1 shadow pt-3 min-vh-100">
          <div class="d-flex flex-column align-items-center">
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="java($event)">
              <img src="./icons/java.svg" width="50" alt="java"></img>
            </button>
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="c($event)">
              <img src="./icons/32px-C_Language_Logo.svg.png" width="50" alt="c"></img>
            </button>
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="cpp($event)">
              <img src="./icons/cpp.svg" width="50" alt="cpp"></img>
            </button>
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="csharp($event)"> 
              <img src="./icons/csharp.svg" width="50" alt="csharp"></img>
            </button>
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="python($event)">
              <img src="./icons/python.svg" width="50" alt="python"></img>
            </button>
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="ruby($event)">
              <img src="./icons/ruby.svg" width="50" alt="ruby"></img>
            </button>
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="go($event)" disabled>
              <img src="./icons/go.svg" width="50" alt="go"></img>
            </button>
            <button class="btn btn-outline-secondary mb-2 btn-lang" v-on:click="r($event)">
              <img src="./icons/r.svg" width="50" alt="go"></img>
            </button>
          </div>
        </nav>
        <div class="col min-vh-100 max-vh-100">
          <div class="d-flex flex-row align-items-center justify-content-between my-2">
            <div class="font-weight-bold h5">
              Input
            </div>
            <div class="d-flex flex-row align-items-center ">
                <label for="name" class="mx-1 text-muted">Name</label>
                <input type="text" id="name" class="form-control mx-1" v-model="fileName">
                <button class="btn btn-primary mx-3" v-on:click="run()">RUN</button>
              </div>
          </div>
          <div id="editor" ></div>
        </div>
        <div class="col">
          <div class="d-flex flex-row align-items-center justify-content-between my-2">
            <div class="h5">
              Output
            </div>
            <div>
              <button class="btn btn-primary" v-on:click="clearOutput()">Clear</button>
            </div>
          </div>
          <textarea name="content" class="output-container" v-model="post" readonly></textarea>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { EditorState } from "@codemirror/state";
import { EditorView, keymap } from "@codemirror/view";
import { defaultKeymap, insertTab, indentLess } from "@codemirror/commands";
import { basicSetup } from "codemirror";
import { java } from "@codemirror/lang-java";
import "./App.css";

export default {
  name: "App",
  mounted() {
    const targetElement = document.querySelector("#editor");
    const fixedHeightEditor = EditorView.theme({
      ".cm-scroller": { overflow: "false" },
    });

    let startState = EditorState.create({
      doc: "Welcome to Online Compiler\n\n" + "Choose a language to start program!",
      extensions: [
        java(),
        keymap.of(defaultKeymap),
        keymap.of([
          {
            key: "Tab",
            preventDefault: true,
            run: insertTab,
          },
          {
            key: "Shift-Tab",
            preventDefault: true,
            run: indentLess,
          },
        ]),
        EditorState.tabSize.of(2),
        basicSetup,
        fixedHeightEditor,
      ],
    });

    this.editor = new EditorView({
      state: startState,
      parent: targetElement,
    });
  },
  data() {
    return {
      editor: null,
      fileName: "",
      fileExtension: "",
      language:"",
      post: ""
    }
  },
  methods: {
    run(){
      this.post = "";
      fetch('http://localhost:8080', {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          fileName: this.fileName,
          fileExtension: this.fileExtension,
          language: this.language,
          sourceCode: this.editor.state.doc.toString()
        })
      })
        .then(response => response.json())
        .then(data => {
          for(let i=0;i<data.output.length;i++){
            this.post += data.output[i]+"\n";
          }
        })
        
    },
    java(event){
      this.select(event);
      let code = 'public class HelloWorld {\n'
      + '\tpublic static void main(String[] args) {\n'
      + '\t\tSystem.out.println("Hello World!");\n'
      + '\t}\n}'
      //change code
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".java";
      this.language="java";

      //change info
    },
    c(event){
      this.select(event);
      let code = '#include <stdio.h>\n\n'
        +'int main() { \n'
        +'\tprintf("Hello World!"); \n'
        +'\treturn 0;\n'
        +'};';
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".c";
      this.language="c";
    },
    cpp(event){
      this.select(event);
      let code = '#include <iostream>\n\n'
        +'int main() {\n'
        +'\tstd::cout << "Hello World!";\n'
        +'\treturn 0;\n'
        +'}';
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".cpp";
      this.language="cpp";
    },
    csharp(event){
      this.select(event);
      let code = 'using System;\n\n'
        +'public class HelloWorld\n'
        +'{\n'
        +'\tpublic static void Main(string[] args)\n'
        +'\t{\n'
        +'\t\tConsole.WriteLine ("Hello World!");\n'
        +'\t}\n'
        +'}';
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".cs";
      this.language="csharp";
    },
    python(event){
      this.select(event);
      let code = 'print("Hello World!")';
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".py";
      this.language="python";
    },
    ruby(event){
      this.select(event);
      let code = "puts 'Hello world!'";
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".rb";
      this.language="ruby";
    },
    go(event){
      this.select(event);
      let code = 'package main\n'+
      'import "fmt"\n'+
      'func main() {\n'+
	      '\tfmt.Println("hello world!")\n'+
      '}';
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".go";
      this.language="go";
    },
    r(event){
      this.select(event);
      let code = 'print("Hello World!")';
      this.editor.dispatch({
        changes: {from: 0, to: this.editor.state.doc.length, insert: code},
      });
      this.fileName= "HelloWorld";
      this.fileExtension= ".R";
      this.language="r";
    },
    select(event){
      var activeBtns = document.getElementsByClassName("active");
      for(let i = 0 ; i< activeBtns.length; i++) {
        activeBtns[i].classList.remove("active");
      }
      if(event.target instanceof HTMLImageElement) {
        event.target.parentElement.classList.add("active");
      } else{
        event.target.classList.add("active");
      }
      console.log(event.target);
    },
    clearOutput(){
      this.post="";
    }
  }

  
};

</script>
<style lang=""></style>
btn-lang