const csrfToken = document.getElementById('csrf-token').value

const ce = React.createElement

class Header extends React.Component {
    render() {
        const {children} = this.props;
        return ce('header',null,
            ce('h1',{onClick: () => window.location.href = "/"},'Spinify'),
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
    render() {
        return ce('div',null,
            ce(Header,null,
                ce('button',{onClick: () => window.location.href = '/logout'},'Logout')
            ),
            ce(SideBar,null,
                ce('div',null,null)   
            )
        )
    }
}

ReactDOM.render(
    ce(ProfilePage),
    document.getElementById('root')
)