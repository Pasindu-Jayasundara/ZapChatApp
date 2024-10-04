import { Pressable, StyleSheet, Text, View } from "react-native";
import { useEffect, useState } from "react";

export function Tab({ func }) {

    const [getCategory, setCategory] = useState("chat")

    const pressCategory = (category) => {
        setCategory(category)
    }

    useEffect(() => {

        func(getCategory)

    }, [getCategory])

    return (
        <View style={styles.container}>
            <Pressable onPress={() => { pressCategory("chat") }} style={styles.button} >
                <Text style={getCategory=="chat"?styles.active:""}>Chat</Text>
            </Pressable>
            <Pressable onPress={() => { pressCategory("group") }} style={styles.button}>
                <Text style={getCategory=="group"?styles.active:""}>Group</Text>
            </Pressable>
            <Pressable onPress={() => { pressCategory("status") }} style={styles.button}>
                <Text style={getCategory=="status"?styles.active:""}>Status</Text>
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