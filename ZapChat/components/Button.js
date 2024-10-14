import { useEffect, useState } from "react";
import { Pressable, StyleSheet, Text } from "react-native";

export function Button({ text, style, func,textStyle={} }) {

    const [getText, setText] = useState("");
    const [getStyle, setStyle] = useState({});

    useEffect(() => {
        setText(text);
        setStyle(style);
    }, [text, style]);

    const handlePress = () => {
        if (typeof func === "function") {
            func(); 
        } else {
            console.warn("Provided 'func' prop is not a function");
        }
    };

    return (

        <Pressable style={[styles.pressable, getStyle]} onPress={handlePress}>
            <Text style={[styles.buttonText,textStyle]}>{getText}</Text>
        </Pressable>
    );
}

const styles = StyleSheet.create({
    pressable: {
        backgroundColor: "black",
        width: "100%",
        justifyContent: "center",
        alignItems: "center",
        paddingVertical: 12,
        borderRadius: 5
    },
    buttonText: {
        color: "white",
        fontSize: 15
    }
})