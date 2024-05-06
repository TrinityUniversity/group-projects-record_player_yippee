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
        return (
        ce('div',{className: 'login-page'},
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
        )
    }
    nameChangeHandler = (e) => {
        this.setState({loginName: e.target.value})
    }

    passChangeHandler = (e) => {
        this.setState({loginPass: e.target.value})
    }

    login = () => {
        const alerts = []
        let dropLogin = false
        if(this.state.loginName == "" || this.state.loginPass == ""){
            alerts.push("Username and Password must not be blank")
            dropLogin = true
        }
        if(!dropLogin){
            fetch(loginRoute,{
                method: "POST",
                headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
                body: JSON.stringify({username: this.state.loginName, password: this.state.loginPass})
                }
            ).then(res => res.json()).then(valid => {
                if(valid){
                    window.location.href = '/'
                }else{
                    alerts.push("Invalid Username or Password")
                    this.props.alertUpdate(alerts)
                }
            })
        }
        this.props.alertUpdate(alerts)
    }

}

class SignUp extends React.Component {
    constructor(props){
        super(props),
        this.state = {signUpName: "",signUpPass: "",signUpPassVer: ""}
    }
    render(){
        return ce('div',{className: 'login-page'},
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
        const alerts = []
        let dropSignUp = false
        if(this.state.signUpName == "" || this.state.signUpPass == "" || this.state.signUpPassVer == ""){
            alerts.push("Username and Passwords must not be blank")
            dropSignUp = true
        }
        if(this.state.signUpName != "" && this.state.signUpName.match(/^[a-zA-Z0-9!$_\-]+$/) == null){
            alerts.push("Username can only contain letters, numbers, and the following symbols: -,_,!,$")
            dropSignUp = true
        }
        if(this.state.signUpPass != "" && this.state.signUpPass.match(/^[a-zA-Z0-9!@#$%^&*]/) == null){
            alerts.push("Password can only contain letters, numbers, and symbols")
            dropSignUp = true
        }
        if(this.state.signUpPassVer != "" & this.state.signUpPassVer.match(/^[a-zA-Z0-9!@#$%^&*]/) == null){
            alerts.push("Password Verification can only contain letters, numbers, and symbols")
            dropSignUp = true
        }
        if(this.state.signUpPass !== this.state.signUpPassVer){
            alerts.push("Passwords must match")
            dropSignUp = true
        }
        if(!dropSignUp){
            fetch(signUpRoute,{
                method: "POST",
                headers: {'Content-Type':'application/json','Csrf-Token': csrfToken},
                body: JSON.stringify({username: this.state.signUpName.trim().replace(/\s/g,''), password: this.state.signUpPass})
                }
            ).then(res => res.json())
            .then(valid => {
                if(valid){
                    window.location.href = '/'
                }else{
                    alerts.push("Username already exists")
                    this.props.alertUpdate(alerts)
                }
            })
        }
        this.props.alertUpdate(alerts)
    }
}

class LoginPage extends React.Component {
    constructor(props){
        super(props),
        this.state = {signUp: false,alerts: []}
    }
    render(){
        return ce('div', null,
            ce(Header,null,
                this.state.signUp?
                ce('button',{onClick: () => this.setState({signUp: false, alerts:[]})},"Sign In")
                :
                ce('button',{onClick: () => this.setState({signUp: true,alerts:[]})},"Sign Up")
            ),
            ce('div',{className:"login-page-container"},
                this.state.signUp?
                ce(SignUp, {alertUpdate: (alerts) => this.setState({alerts})})
                :
                ce(Login, {alertUpdate: (alerts) => this.setState({alerts})}),
                ce('div',{className:'alerts'},
                    this.state.alerts.map(a => ce('p',{id:'alerts'},a))
                )
            )
        )
    }
}

ReactDOM.render(
    ce(LoginPage),
    document.getElementById('root')
)