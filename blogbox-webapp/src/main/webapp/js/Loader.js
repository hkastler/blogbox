class Loader{
    constructor(){
       this.container = document.createElement("div");
    }
    dots(){
        this.container.id = "loader";
        this.container.className = "loading-dots";
        for(let i = 1; i <= 3; i++){
            let h1 = document.createElement("h1");
            h1.classList.add("dot");
            h1.classList.add("dot"+i);
            h1.innerHTML = ".";
            this.container.appendChild(h1);
        }
        return this.container;
    }

    show(){
        this.container.classList.remove("hide");
        this.container.classList.add("show");
    }
    hide(){
        this.container.classList.remove("show");
        this.container.classList.add("hide");
    }
}
export default Loader;