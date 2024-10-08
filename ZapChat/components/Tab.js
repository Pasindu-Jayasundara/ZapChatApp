import { Pressable, StyleSheet, Text, View } from "react-native";
import { useEffect, useState } from "react";

export function Tab({ setFunc,getFunc }) {

    // const [getCategory, setCategory] = useState("chat")
    // const [getCategory, setCategory] = useState("group")

    // const pressCategory = (category) => {
    //     func(category)
    //     setCategory(category)
    // }

    // useEffect(() => {

    //     func(getCategory)

    // }, [getCategory])

    return (
        <View style={styles.container}>
            <Pressable onPress={() => { setFunc("chat") }} style={styles.button} >
                <Text style={getFunc=="chat"?styles.active:""}>Chat</Text>
            </Pressable>
            <Pressable onPress={() => { setFunc("group") }} style={styles.button}>
                <Text style={getFunc=="group"?styles.active:""}>Group</Text>
            </Pressable>
            <Pressable onPress={() => { setFunc("status") }} style={styles.button}>
                <Text style={getFunc=="status"?styles.active:""}>Status</Text>
            </Pressable>
        </View>
    )
}

const styles = StyleSheet.create({
    active:{
        color:"#ff5b6b"
    },
    button: {
        paddingHorizontal: 10,
        paddingVertical: 5
    },
    container: {
        width: "100%",
        flexDirection: "row"
    }
})