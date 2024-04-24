const csrfToken = document.getElementById('csrf-token').value
const userDataRoute = document.getElementById('user-data-route').value
const collectionsRoute = document.getElementById('collections-route').value
const getCollectionRoute = document.getElementById('get-collection-route').value
const removeRoute = document.getElementById('remove-route').value
const removeCollectionRoute = document.getElementById('remove-collection-route').value
const playSVG = document.getElementById('play-svg').value

const ce = React.createElement

class Header extends React.Component {
    render() {
        const {children} = this.props;
        return ce('header',null,
            ce('h1',{onClick: () => window.location.href = "/",className:"title"},'Spinify'),
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

class CollectionDisplay extends React.Component {
    constructor(props){
        super(props),
        this.state = {records:[]}
    }
    componentDidUpdate = (prevProps) => {
        if(!this.props.collectionId && prevProps.collectionId != this.props.collectionId)this.setState({records:[]})
        else if(this.props.collectionId && prevProps.collectionId != this.props.collectionId) this.loadCollection()
    }
    render(){
        return(
            ce('div',{className:"collection-records"},
                this.state.records.map((rec,index) => {
                    return(
                        ce('div',{key:index,className:"collection-record"},
                            ce('p',{className:"x-button",onClick:()=> this.removeRecord(rec)},'x'),
                            ce('p',{className:"collection-record-name"},`${rec.name} - ${rec.artist}`),
                            ce('p',{className:"collection-record-username"},`"${rec.creatorName}'s Version"`),
                            ce('button',{className:"play-button",onClick: () => this.playRecord(rec)},ce('img',{src:playSVG}))
                        )
                    )
                })
            )
        )
    }

    loadCollection = () => {
        fetch(`${getCollectionRoute}${this.props.collectionId}`).then(res => res.json()).then(records => {this.setState({records})})
    }

    removeRecord = (record) =>{
        fetch(removeRoute,{
            method:"POST",
            headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
            body: JSON.stringify({record,collectionId:this.props.collectionId})
        }).then(res => res.json()).then(res=> {
            if(res){
                this.loadCollection()
                this.props.fixScreen()
            }
        })
    }

    playRecord = (record) => {
        window.localStorage.setItem('current-record',JSON.stringify(record))
        window.location.href = '/'
    }
}

class ProfilePage extends React.Component {
    constructor(props){
        super(props),
        this.state = {userData: {}, collections: [], selectedCollection:{}}
    }

    componentDidMount = () => {
        this.getUserData()
        this.loadCollections(true)
    }

    render() {
        return ce('div',null,
            ce(Header,null,
                ce('h2',{className:"current-song"},this.state.selectedCollection?.name),
                ce('button',{onClick: () => window.location.href = '/logout'},'Logout')
            ),
            ce(SideBar,null,
                ce('div',{className: "profile-card"},
                    Object.entries(this.state.userData).map(([k,v],index) => {
                        return ce('p',{key:index},`${k.toUpperCase().replace('_',' ')}: ${v}`)
                    })
                ),
                ce('div',{className:"collections"},
                    ce('h3',null,"Collections"),
                    this.state.collections.map((coll,index) => {
                        return(ce('div',{key:index, className:"sidebar-collection"},
                            ce('p',{className:'x-button',onClick:()=>this.removeCollection(coll.id)},'x'),
                            ce('div',{className:"inner-sidebar-collection",onClick: () => this.setState({selectedCollection:coll})},
                                ce('p',null,coll.name),
                                ce('p',null,`Number of Items: ${coll.numOfItems}`))
                            )
                        )
                    }
                    )
                )
            ),
            ce(CollectionDisplay,{collectionId:this.state.selectedCollection?.id,fixScreen:()=>{this.getUserData();this.loadCollections(false)}},null)
        )
    }

    getUserData = () => {
        fetch(userDataRoute).then(res => res.json()).then(userData => this.setState({userData}))
    }

    loadCollections = (updateSelected) => {
        fetch(collectionsRoute).then(res => res.json()).then(res =>{
            if(res.length == 0)this.setState({collections:res,selectedCollection:{}})
            else{
                if(updateSelected)this.setState({collections: res,selectedCollection:res[0]})
                else this.setState({collections: res})
            }
        })
    }

    removeCollection = (id) => {
        fetch(removeCollectionRoute,{
            method:"POST",
            headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
            body: JSON.stringify({id})
        }).then(res => res.json()).then(res => {if(res)this.loadCollections((this.state.selectedCollection.id == id))})
    }
}

ReactDOM.render(
    ce(ProfilePage),
    document.getElementById('root')
)