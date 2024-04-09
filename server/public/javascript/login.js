const loginRoute = document.getElementById('login-route').value
const signUpRoute = document.getElementById('sign-up-route').value
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

class Login extends React.Component {
    constructor(props){
        super(props),
        this.state = {loginName: "",loginPass: ""}
    }
    render(){
        return ce('div',null,
            ce('h2',null,'Login'),
            ce('label',null,'Username'),
            ce('br'),
            ce('input',{type: 'text', onChange: (e) => this.nameChangeHandler(e),value: this.state.loginName}),
            ce('br'),
            ce('label',null,'Password'),
            ce('br'),
            ce('input',{type: 'password', onChange: (e) => this.passChangeHandler(e),value: this.state.loginPass}),
            ce('br'),
            ce('button',{onClick: (e) => this.login(e)},'Login')
        )
    }
    nameChangeHandler = (e) => {
        this.setState({loginName: e.target.value})
    }

    passChangeHandler = (e) => {
        this.setState({loginPass: e.target.value})
    }

    login = (e) => {
        fetch(loginRoute,{
            method: "POST",
            headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
            body: JSON.stringify({username: this.state.loginName, password: this.state.loginPass})
            }
        ).then(res => res.json()).then(body => console.log(body))
    }

}

class SignUp extends React.Component {
    constructor(props){
        super(props),
        this.state = {signUpName: "",signUpPass: "",signUpPassVer: ""}
    }
    render(){
        return ce('div',null,
            ce('h2',null,'Sign Up'),
            ce('label',null,'Username'),
            ce('br'),
            ce('input',{type: 'text', onChange: (e) => this.nameChangeHandler(e),value: this.state.signUpName}),
            ce('br'),
            ce('label',null,'Password'),
            ce('br'),
            ce('input',{type: 'password', onChange: (e) => this.passChangeHandler(e),value: this.state.signUpPass}),
            ce('br'),
            ce('label',null,'Re-enter Password'),
            ce('br'),
            ce('input',{type:'password',onChange: (e) => this.passVerChangeHandler(e),value: this.state.signUpPassVer}),
            ce('br'),
            ce('button',{onClick: (e) => this.signUp(e)},'Sign Up')
        )
    }
    nameChangeHandler = (e) => {
        this.setState({signUpName: e.target.value})
    }

    passChangeHandler = (e) => {
        this.setState({signUpPass: e.target.value})
    }

    passVerChangeHandler = (e) => {
        this.setState({signUpPassVer: e.target.value})
    }

    signUp = (e) => {
        if(this.state.signUpPass === this.state.signUpPassVer){
            fetch(signUpRoute,{
                method: "POST",
                headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
                body: JSON.stringify({username: this.state.signUpName, password: this.state.signUpPass})
                }
            ).then(res => res.json()).then(body => console.log(body))
        }
    }
}

class LoginPage extends React.Component {
    constructor(props){
        super(props),
        this.state = {signUp: false}
    }
    render(){
        return ce('div', null,
            ce(Header,null,
                this.state.signUp?
                ce('p',{onClick: () => this.setState({signUp: false})},"Sign In")
                :
                ce('p',{onClick: () => this.setState({signUp: true})},"Sign Up")
            ),
            this.state.signUp?
            ce(SignUp)
            :
            ce(Login)
        )
    }
}

ReactDOM.render(
    ce(LoginPage),
    document.getElementById('root')
)