const recordsRoute = document.getElementById("get-songs-route").value
const addSongRoute = document.getElementById('add-song-route').value
const csrfToken = document.getElementById('csrf-token').value
const getSongRoute = document.getElementById('get-song-route').value

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
        return( 
        ce('div',null,
            !this.state.adding?
            ce("p",{onClick: () => this.setState({adding:true})},"+")
            :
            ce('div',{ref:this.myRef},
                ce('label',null,'Title:'),
                ce('br'),
                ce('input',{type:'text',value: this.state.name, onChange: (e) => this.nameChangeHandler(e)},null),
                ce('br'),
                ce('label',null,'Mp3 File:'),
                ce('br'),
                ce("input",{type:'file', accept: '.mp3', onChange: (e) => this.fileChangeHandler(e)},null),
                ce('br'),
                ce('button',{onClick: () => {this.addSong();this.setState({adding: false})}},'Add Song!')
            ),
            ce('div',null,
                this.state.records.map((record,index) => 
                    ce("div",{id:record.id, key:index, onClick: () => this.getSong(record)},
                        ce('p',null,`${record.name}`),
                        ce('p',null,`${record.creatorName}'s Version`)
                    )
                )
            )
        ))
    }

    handleOutsideClick = (e) => {
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

    getSong = (record) => {
        fetch(getSongRoute,{
            method: "POST",
            headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
            body: JSON.stringify({path: record.fileLocation})
        })
        .then(res => {
            if(res.status == 200){
                res.blob().then(blob => {
                    console.log(record)
                    this.props.recordUpdate({recordUrl:URL.createObjectURL(blob),record})
                })
            }
        })
        
    }
}

class RecordPlayer extends React.Component {
    constructor(props){
        super(props),
        this.state = {playing:false}
    }
    render() {
        return (
            ce('div',null,
                ce('p',{onClick: () => this.handlePlayPause()},this.state.playing?'Pause!':'Play!'),
                ce('audio',{id:"record",src: this.props.recordUrl},null)
            )
        )
    }

    handlePlayPause = () => {
        const audio = document.getElementById('record')
        if(audio.src != window.location.href){
            if(this.state.playing){
                audio.pause()
                this.setState({playing:false})
            }else{
                audio.play()
                this.setState({playing:true})
            }   
        }
    }
}

class HomePage extends React.Component {
    constructor(props){
        super(props),
        this.state = {signUp: false, recordUrl: "/", record:{}}
    }
    render(){
        return ce('div', {className: 'page'}, 
            ce(Header,null,
                ce('div',null,
                    ce('h2',null,this.state.record.name),
                    this.state.record.creatorName?
                    ce('p',null,`${this.state.record.creatorName}'s Version`)
                    :
                    ""
                ),
                ce('button',{onClick: () => window.location.href = "/profile"},'Profile')
            ),
            ce(SideBar,null,ce(RecordsDisplay,{recordUpdate: updates => this.setState(updates)})),
            ce(RecordPlayer, {recordUrl: this.state.recordUrl,record: this.state.record})
        )
    }
}

ReactDOM.render(
    ce(HomePage),
    document.getElementById('root')
)