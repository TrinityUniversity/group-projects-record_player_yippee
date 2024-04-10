const recordsRoute = document.getElementById("get-songs-route").value
const addSongRoute = document.getElementById('add-song-route').value
const csrfToken = document.getElementById('csrf-token').value

const ce = React.createElement

class Header extends React.Component {
    render() {
        const {children} = this.props;
        return ce('header',null,
            ce('h1',null,'Spinify'),
            children
        )
    }
}

class SideBar extends React.Component {
    render() {
        const {children} = this.props;
        return ce('aside',null,
            children
        )
    }
}

class RecordsDisplay extends React.Component {
    constructor(props){
        super(props),
        this.state={
            records: [],
            adding:false,
            file: null,
            name: ""
        }
        this.myRef = React.createRef()
    }
    componentDidMount(){
        this.loadRecords()
        document.addEventListener("mousedown", this.handleOutsideClick);
    }

    render(){
        return ce('div',null,
        !this.state.adding?
        ce("p",{onClick: () => this.setState({adding:true})},"+")
        :
        ce('form',{ref:this.myRef},
            ce('label',null,'Mp3 File:'),
            ce('br'),
            ce("input",{type:'file', accept: '.mp3', onChange: (e) => this.fileChangeHandler(e)},null),
            ce('br'),
            ce('label',null,'Name:'),
            ce('br'),
            ce('input',{type:'text',value: this.state.name, onChange: (e) => this.nameChangeHandler(e)},null),
            ce('br'),
            ce('button',{onClick: () => {this.addSong();this.setState({adding: false})}},'Add Song!')
        ),
        ce('div',null,
            this.state.records.map((record,index) => ce("p",{id:record.id, key:index},record.name))
        )
        )
    }

    handleOutsideClick = (e) => {
        // @ts-ignore
        if(this.myRef.current && !this.myRef.current.contains(e.target)){
            this.setState({adding:false});
        }
    }

    nameChangeHandler = (e) => {
        this.setState({name: e.target.value})
    }

    fileChangeHandler = (e) => {
        this.setState({file: e.target.files[0]})
    }

    loadRecords() {
        fetch(recordsRoute).then(res => res.json()).then(records => {this.setState({records})})
    }

    addSong() {
        console.log(this.state.file)
        const fd = new FormData()
        fd.append('file',this.state.file)
        fd.append('name',this.state.name)
        try{
            fetch(addSongRoute, {
                method: "POST",
                headers: {'Csrf-Token': csrfToken},
                body: fd
            })
            .then(res => res.json())
            .then(data => {if(data){this.loadRecords()}})
        } catch (error){
            console.log('Caught:',error)
        }
    }
}

class HomePage extends React.Component {
    constructor(props){
        super(props),
        this.state = {signUp: false}
    }
    render(){
        return ce('div', {className: 'page'}, 
            ce(Header,null,
                ce('button',null,'Profile')
            ),
            ce(SideBar,null,ce(RecordsDisplay))
        )
    }
}

ReactDOM.render(
    ce(HomePage),
    document.getElementById('root')
)