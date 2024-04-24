const recordsRoute = document.getElementById("get-songs-route").value
const addSongRoute = document.getElementById('add-song-route').value
const csrfToken = document.getElementById('csrf-token').value
const getSongRoute = document.getElementById('get-song-route').value
const likeRoute = document.getElementById("like-route").value
const vinylSVG = document.getElementById('vinyl-svg').value
const starSVG = document.getElementById('star-svg').value
const filledStarSVG = document.getElementById('filled-star-svg').value
const plusSVG = document.getElementById('plus-svg').value
const recordInStorage = window.localStorage.getItem('current-record')

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
            name: "",
            artist: ""
        }
        this.myRef = React.createRef()
    }
    componentDidMount = () => {
        this.loadRecords(true)
        document.addEventListener("mousedown", this.handleOutsideClick);
    }

    componentDidUpdate = (prevProps) =>{
        if(prevProps.counter != this.props.counter){
            this.loadRecords(false)
        }
    }

    render(){
        return( 
        ce('div',null,
            ce('div',{ref:this.myRef,className:"adder",onClick: () => this.setState({adding:true})},
                !this.state.adding?
                ce("p",null,"+")
                :
                ce('div',{className: "adder-form"},
                    ce('label',null,'Title:'),
                    ce('br'),
                    ce('input',{type:'text',value: this.state.name, onChange: (e) => this.nameChangeHandler(e)},null),
                    ce('br'),
                    ce('label',null,'Artist Name:'),
                    ce('br'),
                    ce('input',{type:'text',value:this.state.artist,onChange: (e) => this.artistChangeHandler(e)},null),
                    ce('br'),
                    ce('label',null,'Mp3 File:'),
                    ce('br'),
                    ce("input",{type:'file', accept: '.mp3', onChange: (e) => this.fileChangeHandler(e)},null),
                    ce('br'),
                    ce('button',{onClick: () => {this.addSong();this.setState({adding: false})}},'Add Song!')
                )
                
            ),
            ce('div',{className:"records"},
                ce('h3',null,"Records"),
                this.state.records.map((record,index) => 
                    ce("div",{id:record.id, className:"sidebar-record", key:index, onClick: () => {window.localStorage.setItem('current-record',JSON.stringify(record)),this.getSong(record)}},
                        ce('p',null,`${record.name} - ${record.artist}`),
                        ce('p',null,`"${record.creatorName}'s Version"`)
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

    artistChangeHandler = (e) => {
        this.setState({artist: e.target.value})
    }

    fileChangeHandler = (e) => {
        this.setState({file: e.target.files[0]})
    }

    loadRecords(setStart) {
        fetch(recordsRoute).then(res => res.json()).then(records => {
            this.setState({records});
            if(setStart){
                if(!recordInStorage===null){
                    this.getSong(records[0])
                }else{
                    this.getSong(records.filter(record => record.id == JSON.parse(recordInStorage).id)[0])
                }
            }
        })
    }

    addSong() {
        if(this.state.name !== "" && this.state.artist !== "" && this.state.file !== null){
            const fd = new FormData()
            fd.append('file',this.state.file)
            fd.append('name',this.state.name)
            fd.append('artist',this.state.artist)
            try{
                fetch(addSongRoute, {
                    method: "POST",
                    headers: {'Csrf-Token': csrfToken},
                    body: fd
                })
                .then(res => res.json())
                .then(data => {if(data){this.loadRecords(false);this.setState({adding: false})}})
            } catch (error){
                console.log('Caught:',error)
            }
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
    componentDidUpdate = (prevProps) =>{
        if(prevProps.recordUrl != this.props.recordUrl){
            this.setState({playing:false})
            document.getElementById('record-image').classList.remove('active')
        }
    }
    render() {
        return (
            ce('div',{className: "record"},
                ce('img',{id:"record-image",className:"spin-animation",onClick: ()=>this.handlePlayPause(),src:vinylSVG}),
                ce('audio',{id:"record",src: this.props.recordUrl},null),
            )
        )
    }

    handlePlayPause = () => {
        const audio = document.getElementById('record')
        if(audio.src != window.location.href){
            document.getElementById("record-image").classList.toggle("active")
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
        this.state = {signUp: false, recordUrl: "/", record:{}, likeCounter:0}
    }
    render(){
        return ce('div', {className: 'page'}, 
            ce(Header,null,
                ce('div',{className:"current-song"},
                    this.state.record.name?
                    ce('h2',null,`${this.state.record.name} - ${this.state.record.artist}`)
                    :
                    "",
                    this.state.record.creatorName?
                    ce('p',null,`"${this.state.record.creatorName}'s Version"`)
                    :
                    ""
                ),
                ce('button',{onClick: () => window.location.href = "/profile"},'Profile')
            ),
            ce(SideBar,null,ce(RecordsDisplay,{recordUpdate: updates => this.setState(updates),counter:this.state.likeCounter})),
            this.state.record.name?
            ce(RecordPlayer, {recordUrl: this.state.recordUrl,record: this.state.record})
            :
            "",
            this.state.record.name?
            ce('img',{className:"like",src:this.state.record.liked?filledStarSVG:starSVG,onClick:() => this.handleLike()})
            :
            "",
            this.state.record.name?
            ce('img',{className:"plus-button",src:plusSVG})
            :
            ""
        )
    }

    handleLike = () =>{
        fetch(likeRoute,{
            method:"POST",
            headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
            body: JSON.stringify(this.state.record)
        }).then(res => res.json()).then(res=> {
            if(res){
                this.setState({record:{...this.state.record,liked:!this.state.record.liked},likeCounter:this.state.likeCounter+1})
            }
        })
    }
}

ReactDOM.render(
    ce(HomePage),
    document.getElementById('root')
)