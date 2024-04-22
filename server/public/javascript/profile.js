const csrfToken = document.getElementById('csrf-token').value
const userDataRoute = document.getElementById('user-data-route').value

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

class ProfilePage extends React.Component {
    constructor(props){
        super(props),
        this.state = {userData: {}}
    }

    componentDidMount = () => {
        this.getUserData()
    }

    render() {
        return ce('div',null,
            ce(Header,null,
                ce('button',{onClick: () => window.location.href = '/logout'},'Logout')
            ),
            ce(SideBar,null,
                ce('div',{className: "profile-card"},
                    Object.entries(this.state.userData).map(([k,v],index) => {
                        return ce('p',{key:index},`${k.toUpperCase().replace('_',' ')}: ${v}`)
                    })
                )   
            )
        )
    }

    getUserData = () => {
        fetch(userDataRoute).then(res => res.json()).then(userData => this.setState({userData}))
    }
}

ReactDOM.render(
    ce(ProfilePage),
    document.getElementById('root')
)