import React, {Component} from 'react';
import {withStyles} from "@material-ui/core";
import PropTypes from "prop-types";
import Grid from "@material-ui/core/Grid/Grid";
import Paper from "@material-ui/core/Paper/Paper";
import ResultsChart from "./components/ResultsChart";
import InfoIcon from '@material-ui/icons/Info';
import IconButton from "@material-ui/core/IconButton/IconButton";
import InfoDialog from "../../../components/InfoDialog";
import Typography from "@material-ui/core/Typography";
import * as LongStrings from "../../../components/LongStrings";
import ReactGA from 'react-ga';
import connect from "react-redux/es/connect/connect";

const styles = theme => ({
    mainDiv: {
        paddingTop: theme.spacing.unit * 2.5,
        paddingBottom: theme.spacing.unit * 5.5,
        textAlign: "center",
    },

    gridItem: {
        maxWidth: theme.spacing.unit * 75,
        minWidth: theme.spacing.unit * 38,
        margin: theme.spacing.unit * 2,
    },

    infoButton: {
        bottom: theme.spacing.unit * 0.25,
        left: theme.spacing.unit * 1.2,
    },

    gridItem_title: {
        margin: theme.spacing.unit * 1,
        paddingTop: theme.spacing.unit * 1,
    },
});



class Result extends Component {

    constructor(props) {
        super(props);
        this.hideInfo = this.hideInfo.bind(this);
        this.showInfo = this.showInfo.bind(this);

        this.state = {
            optionsInfo: false,
            criteriaInfo: false,
            isLoading: true,
            showOptionsRanking: true,
            showCriteriaRanking: true,
        };
    }

    //Refresh when redux state changes
    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.app.isFetchingDataPackage !== this.props.app.isFetchingDataPackage) {

            if (this.props.app.isFetchingDataPackage > 0){
                this.setState({
                    showOptionsRanking: false,
                    showCriteriaRanking: false,
                });
            }else {

                this.setState({
                    showOptionsRanking: true,
                });

                //Timer to avoid chrome breaking animations
                setTimeout(() => {
                    this.setState({showCriteriaRanking: true});
                }, 1000);

            }


        }
    }


    hideInfo(e, name) {
        this.setState({[name]: false,});

        ReactGA.event({
            category: 'Result',
            action: 'Hide Info from ' + name,
        });
    };

    showInfo(e, name){
        this.setState({ [name]: true });

        ReactGA.event({
            category: 'Result',
            action: 'Show Info from ' + name,
        });
    };


    render() {

        const {classes} = this.props;

        return (

            <div className={classes.mainDiv}>
                <Grid container justify="center" alignContent='center' spacing={24}>
                    <Grid className={classes.gridItem} key="1" item xs={12}>
                        {this.state.showOptionsRanking &&
                        <Paper className={classes.paper} elevation={2} key={"Option"}>
                            <Typography variant="h5" className={classes.gridItem_title} gutterBottom>
                                Decision Options Ranking
                                <IconButton
                                    aria-label="Help"
                                    className={classes.infoButton}
                                    onClick={(e) => this.showInfo(e,"optionsInfo")}>
                                    <InfoIcon color="secondary"/>
                                </IconButton>
                            </Typography>

                            <ResultsChart
                                itemsKey={"decisionOptions"}
                                decisionId={this.props.decisionId}
                                YKey={"name"}/>
                        </Paper>
                        }
                    </Grid>
                    <Grid className={classes.gridItem} key="2" item xs={12}>
                        {this.state.showCriteriaRanking &&
                        <Paper className={classes.paper} elevation={2} key={"Criteria"}>
                            <Typography variant="h5" className={classes.gridItem_title} gutterBottom>
                                Selection Criteria Ranking
                                <IconButton
                                    aria-label="Help"
                                    className={classes.infoButton}
                                    onClick={(e) => this.showInfo(e, "criteriaInfo")}>
                                    <InfoIcon color="secondary"/>
                                </IconButton>
                            </Typography>

                            <ResultsChart
                                itemsKey={"selectionCriteria"}
                                decisionId={this.props.decisionId}
                                YKey={"name"}/>
                        </Paper>
                        }
                    </Grid>
                </Grid>
                {/*Info Dialogs*/}
                <InfoDialog
                    title={"Decision Options Ranking"}
                    text={LongStrings.OptionsResultInfo}
                    show={this.state.optionsInfo}
                    hide={(e) => this.hideInfo(e,"optionsInfo")}
                />
                <InfoDialog
                    title={"Selection Criteria Ranking"}
                    text={LongStrings.CriteriaResultInfo}
                    show={this.state.criteriaInfo}
                    hide={(e) => this.hideInfo(e,"criteriaInfo")}
                />
            </div>
        )
    }
}

Result.propTypes = {
    classes: PropTypes.object.isRequired,
    app: PropTypes.object.isRequired
};

const mapStateToProps = state => ({
    app: state.app
});

export default connect(mapStateToProps, {})(withStyles(styles)(Result));