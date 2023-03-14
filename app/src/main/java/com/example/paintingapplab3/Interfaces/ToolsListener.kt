package com.example.paintingapplab3.Interfaces

import com.example.paintingapplab3.model.Operation

interface ToolsListener {
    fun onSelected(operation : Operation)
}