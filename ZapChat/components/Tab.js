import { Pressable, StyleSheet, Text, View } from "react-native";

export function Tab() {

    return (
        <View style={styles.container}>
            <Pressable style={styles.button}>
                <Text>Chat</Text>
            </Pressable>
            <Pressable style={styles.button}>
                <Text>Group</Text>
            </Pressable>
            <Pressable style={styles.button}>
                <Text>Status</Text>
            </Pressable>
        </View>
    )
}

const styles = StyleSheet.create({
    button: {
        paddingHorizontal: 10,
        paddingVertical: 5
    },
    container: {
        width: "100%",
        flexDirection: "row"
    }
})