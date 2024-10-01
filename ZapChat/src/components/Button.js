import { useEffect, useState } from "react";
import { Pressable, StyleSheet, Text } from "react-native";

export function Button({text,style}){

    const [getText, setText] = useState("");

    useEffect(() => {
        setText(text)
    }, [text,style])

    return(
        <Pressable style={[styles.pressable,style]}>
            <Text style={styles.buttonText}>{getText}</Text>
        </Pressable>
    );
}

const styles=StyleSheet.create({
    pressable:{
        backgroundColor:"black",
        width:"100%",
        justifyContent:"center",
        alignItems:"center",
        paddingVertical:12,
        borderRadius:5
    },
    buttonText:{
        color:"white",
        fontSize:15
    }
})