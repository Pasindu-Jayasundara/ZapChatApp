import { Text, View } from "react-native"
import { StyleSheet } from "react-native"
import { useEffect, useState } from "react";
import Animated, { FadeIn, FadeOut } from "react-native-reanimated";

const AnimatedView = Animated.createAnimatedComponent(View);

export function Date({date}) {

    const [getDate,setDate]=useState("")

    useEffect(() => {
        
        setDate(date)

    }, [date])

    return (
        <AnimatedView entering={FadeIn} exiting={FadeOut} style={styles.container}>
            <Text style={styles.date}>{getDate}</Text>
        </AnimatedView>
    )
}

const styles = StyleSheet.create({
    container: {
        alignSelf: "center",
        backgroundColor:"black",
        paddingHorizontal:10,
        paddingVertical:5,
        borderRadius:5,
        marginVertical:20,
    },
    date:{
        color:"white"
    },
})