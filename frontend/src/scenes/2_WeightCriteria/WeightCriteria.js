import React, {Component} from 'react';
import Grid from "@material-ui/core/Grid/Grid";
import Paper from "@material-ui/core/Paper/Paper";
import PropTypes from "prop-types";
import {withStyles} from "@material-ui/core";
import Slider from "@material-ui/lab/Slider";
import update from 'immutability-helper';
import WeightSlider_Scale from "../../images/WeightSlider_Scale.svg"
import Typography from "@material-ui/core/Typography/Typography";
import InfoIcon from '@material-ui/icons/Info';
import IconButton from "@material-ui/core/IconButton/IconButton";
import InfoDialog from "../../components/InfoDialog";
import {connect} from "react-redux";
import {get_criteria, change_criteria, send_every_criteria} from "../../services/actions/WeightCriteria_Action";
import * as LongStrings from "../../components/LongStrings";
import ReactGA from 'react-ga';

const styles = theme => ({
    root: {
        flexGrow: 1,
        align: 'center',
        overflowX: 'hidden', //Avoid negative margin from mainGrid
    },

    mainDiv: {
        paddingTop: theme.spacing.unit * 2.5,
        paddingBottom: theme.spacing.unit * 5.5,
    },

    infoButton: {
        bottom: theme.spacing.unit * 0.25,
        left: theme.spacing.unit * 1.2,
    },

    titleText: {
        textAlign: "center",
    },

    paperDiv: {
        paddingBottom: theme.spacing.unit * 1,
    },

    cellTitle: {
        width: theme.spacing.unit * 100,
    },

    cellCriteria: {
        minWidth: theme.spacing.unit * 40,
        maxWidth: theme.spacing.unit * 50,
        alignContent: 'center',
        textAlign: 'center',
    },

    slider: {
        opacity: 1,
        backgroundSize: 'cover',
        backgroundRepeat: 'noRepeat',
        backgroundPosition: 'center',
    },
    sliderContainer: {
        paddingTop: theme.spacing.unit * 2,
        paddingBottom: theme.spacing.unit * 2,
        paddingLeft: 0,
        paddingRight: 0,
        backgroundSize: '100% ',
        backgroundImage: 'url(' + WeightSlider_Scale + ')',
        backgroundRepeat: 'noRepeat',
        backgroundPosition: 'center',
    },
    sliderScale: {

        marginTop: -theme.spacing.unit * 2,
        marginBottom: -theme.spacing.unit * 2,
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        alignContent: 'center',
        textAlign: 'center',
    },
    track: {
        opacity: 0.7,
    },


    emptyLine: {
        height: theme.spacing.unit * 4,
    },

});



class WeightCriteria extends Component {

    constructor(props) {
        super(props);

        this.state = {
            weightedCriteria: [],
            weightInfo: [],
            value: 50,
            showInfo: false,
            dragging: false,
        };


        this.onChange = this.onChange.bind(this);
        this.onDragEnd = this.onDragEnd.bind(this);
        this.onHideInfo = this.onHideInfo.bind(this);
        this.onShowInfo = this.onShowInfo.bind(this);

    }


    //GET_CRITERIA
    async componentDidMount() {
        await this.props.get_criteria(this.props.projectId);
    }

    async componentWillUnmount() {
        await this.props.send_every_criteria(this.state.weightedCriteria);
    }

    //Refresh when redux state changes
    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.weightCriteria.weightedCriteria !== this.props.weightCriteria.weightedCriteria) {

            this.setState({weightedCriteria: this.props.weightCriteria.weightedCriteria});

            //Load InfoText at start
            if (this.state.weightInfo.length === 0 && this.props.weightCriteria.weightedCriteria.length > 0) {

                let weightInfoArray = this.state.weightInfo;

                this.props.weightCriteria.weightedCriteria.forEach(function (criteria, index) {
                    weightInfoArray[index] = WeightCriteria.getWeightInfoText(criteria);
                });

                this.setState({
                    weightInfo: weightInfoArray,
                });

                if(this.props.weightCriteria.weightedCriteria !== null){
                    ReactGA.event({
                        category: 'Weight Criteria',
                        action: 'Items Number',
                        value: this.props.weightCriteria.weightedCriteria.length,
                    });
                }
            }
        }

        //Go to decisions after asking to save project
        if (prevState.weightedCriteria !== !this.state.weightedCriteria) {
            console.log("data");
            // this.props.change_criteria(itemLocal);
        }
    }


    //CHANGE_CRITERIA
    onDragEnd(itemLocal) {
        //Will be used when bug from slider in MaterialUI is repaired
        //this.props.change_criteria(itemLocal);

        ReactGA.event({
            category: 'Weight Criteria',
            action: 'Drag End'
        });
    }


    onChange = (event, value, itemLocal, index) => {

        let weightInfoArray = this.state.weightInfo;

        //Save selected criteria if left or right
        let selectedCriteria = (value < 0) ? itemLocal.selectionCriteria1 : itemLocal.selectionCriteria2;

        //Update State
        let newState = update(this.state.weightedCriteria, {
            [index]: {
                weight: {$set: value},
                selectedCriteria: {$set: selectedCriteria}
            }
        });


        //Show InfoText
        weightInfoArray[index] = WeightCriteria.getWeightInfoText(itemLocal);

        this.setState({
            weightedCriteria: newState,
            weightInfo: weightInfoArray,
        });


    };

    onHideInfo() {
        this.setState({showInfo: false,});

        ReactGA.event({
            category: 'Weight Criteria',
            action: 'Hide Info'
        });
    };

    onShowInfo = () => {
        this.setState({showInfo: true,});

        ReactGA.event({
            category: 'Weight Criteria',
            action: 'Show Info'
        });
    };


    static getWeightInfoText(itemLocal) {
        switch (true) {
            case (itemLocal.weight < -66):
                return `${itemLocal.selectionCriteria1.name} is way more important than ${itemLocal.selectionCriteria2.name}`;
            case (itemLocal.weight < -33):
                return `${itemLocal.selectionCriteria1.name} is more important than ${itemLocal.selectionCriteria2.name}`;
            case (itemLocal.weight < -5):
                return `${itemLocal.selectionCriteria1.name} is a little more important than ${itemLocal.selectionCriteria2.name}`;
            case (itemLocal.weight < 5):
                return `${itemLocal.selectionCriteria1.name} is as important as ${itemLocal.selectionCriteria2.name}`;
            case (itemLocal.weight < 33):
                return `${itemLocal.selectionCriteria2.name} is a little more important than ${itemLocal.selectionCriteria1.name}`;
            case (itemLocal.weight < 66):
                return `${itemLocal.selectionCriteria2.name} is more important than ${itemLocal.selectionCriteria1.name}`;
            case (itemLocal.weight <= 100):
                return `${itemLocal.selectionCriteria2.name} is way more important than ${itemLocal.selectionCriteria1.name}`;
            default:
                return "";
        }
    }


    render() {

        const {classes} = this.props;


        return (

            <div className={classes.mainDiv}>
                <Grid container justify="center" alignContent='center' spacing={24}>
                    <Grid item xs={12} className={classes.cellTitle}>
                        <Typography variant="h5" className={classes.titleText} gutterBottom>
                            Weight Criteria
                            <IconButton aria-label="Help" className={classes.infoButton} onClick={this.onShowInfo}>
                                <InfoIcon color="secondary"/>
                            </IconButton>
                        </Typography>
                    </Grid>
                    {this.state.weightedCriteria.map((criteria, index) =>
                        <Grid item xs={6} className={classes.cellCriteria} key={criteria.id}>
                            <Paper className={classes.paper} elevation={2}>
                                <div className={classes.paperDiv}>
                                    <Grid container spacing={16}>
                                        <Grid item xs={6}>
                                            <Typography variant="body1">
                                                {criteria.selectionCriteria1.name}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Typography variant="body1">
                                                {criteria.selectionCriteria2.name}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={12} zeroMinWidth className={classes.sliderScale}>
                                            <Slider
                                                className={classes.slider}
                                                classes={{
                                                    trackAfter: classes.track,
                                                    trackBefore: classes.track,
                                                    container: classes.sliderContainer,
                                                }}
                                                value={criteria.weight}
                                                min={-100}
                                                max={100}
                                                step={1}
                                                onChange={(event, value) => this.onChange(event, value, criteria, index)}
                                                onDragEnd={() => this.onDragEnd(criteria)}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <Typography variant="caption">
                                                {this.state.weightInfo[index]}
                                            </Typography>
                                        </Grid>
                                    </Grid>
                                </div>
                            </Paper>
                        </Grid>
                    )}
                </Grid>
                {/*Empty Line for Buttons*/}
                <div className={classes.emptyLine}/>
                {/*Info Dialogs*/}
                <InfoDialog
                    title={"Weight Criteria"}
                    text={LongStrings.WeightCriteriaInfo}
                    show={this.state.showInfo}
                    hide={this.onHideInfo}
                />
            </div>

        )
    }
}

WeightCriteria.propTypes = {
    classes: PropTypes.object.isRequired,
    weightCriteria: PropTypes.object.isRequired,
    get_criteria: PropTypes.func.isRequired,
    send_every_criteria: PropTypes.func.isRequired,
    change_criteria: PropTypes.func.isRequired,
    optionsAndCriteria: PropTypes.object.isRequired,
};

const mapStateToProps = state => ({
    weightCriteria: state.weightCriteria,
    optionsAndCriteria: state.optionsAndCriteria
});


export default connect(mapStateToProps, {get_criteria, change_criteria,send_every_criteria})(withStyles(styles)(WeightCriteria));
