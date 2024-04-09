const recordsRoute = document.getElementById("get-songs-route").value
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
        this.state={records: []}
    }

    componentDidMount(){
        this.loadRecords()
    }

    render(){
        return ce('div',null,
        ce("p",{onClick: () => this.addSong()},"+"),
        ce('div',null,
            this.state.records.map((record,index) => ce("p",{id:record.id, key:index},record.name))
        )
        )
    }

    loadRecords() {
        fetch(recordsRoute).then(res => res.json()).then(records => {console.log(records); this.setState({records})})
    }

    addSong() {
        const file = document.getElementById("file-input").files[0]
        const name = document.getElementById('song-name').value
        const fd = new FormData()
        fd.append('file',file)
        fd.append('name',name)
        try{
            fetch(songRoute, {
                method: "POST",
                headers: {'Content-Type': 'multipart/form-data', 'Csrf-Token': csrfToken},
                body: fd
            })
            .then(res => res.json())
            .then(data => console.log(data))
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
        return ce('div', null, 
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